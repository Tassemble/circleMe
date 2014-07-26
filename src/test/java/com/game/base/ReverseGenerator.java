package com.game.base;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.game.base.dao.BaseTestCase;
import com.google.common.collect.Lists;
import com.netease.backend.db.result.ColumnMetadata;
import com.netease.framework.dbsupport.SqlManager;
import com.netease.framework.dbsupport.impl.DBResource;

public class ReverseGenerator extends BaseTestCase {

	
	@Autowired
	private SqlManager							sqlManager;
	
	private Logger LOG = LoggerFactory.getLogger(ReverseGenerator.class);
	
	@Test
	public void generate() throws IOException {

		List<String> tables = Arrays.asList("member", "circle_member", "circle_member_relation");
		String packageValue = "org.tassemble.member";
		for (String string : tables) {
			try {
				innerGenerate(string, packageValue, true, false, false);
			} catch (Exception e) {
				 LOG.error("table:" + string + ", error:" + e.getMessage(), e);
			}
		}

//		List<String> courseTables = Arrays.asList("moc_course_measure", "moc_course_setting",
//				"moc_lesson_unit_video_detail");
//		String packageValue = "com.netease.mooc.course";
//		for (String string : courseTables) {
//			try {
//				innerGenerate(string, packageValue);
//			} catch (Exception e) {
//				System.err.println("table:" + string);
//			}
//		}

	}
	
	public List<ColumnMetadata> getMetaData(String table) {
		String sql = "SELECT * FROM " + table + " LIMIT 0";
		DBResource srs = null;
		ResultSet rs = null;
		try {
			srs = sqlManager.executeQuery(sql, new Object[0]);
			ResultSetMetaData metaData = srs.getResultSet().getMetaData();

			int count = metaData.getColumnCount();
			List<ColumnMetadata> columnMetaData = new ArrayList<ColumnMetadata>();
			
			for (int i = 1; i <= count; i++) {
				ColumnMetadata item = new ColumnMetadata(metaData.getColumnName(i),
						metaData.getColumnType(i), 
						metaData.getColumnTypeName(i), 
						metaData.getTableName(i), 
						metaData.getSchemaName(i));
				columnMetaData.add(item);
			}

			return columnMetaData;
		} catch (Throwable e) {
			String msg = "sql:" + sql;

			throw new RuntimeException(msg, e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
				}
		}
	}
	//using abstract;
	public boolean needFilter(String fieldName) {
//		List<String> existedFileds = Arrays.asList("gmt_create", "gmt_modified", "id");
//		if (existedFileds.contains(fieldName)) {
//			return true;
//		}
		return false;
	}
	
	
	public void innerGenerate(String table, String packageValue, boolean replaceDomainIfExist, boolean replaceDaoIfExist, boolean replaceServiceIfExist) throws IOException {
		String domainClass = makeCamelCase(table, true);
		String javaFilePrefix = "./src/main/java";
		String packagePath = packageValue.replaceAll("\\.", File.separator);
		StringBuilder detail = new StringBuilder();
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("${domainClass}", domainClass);
		variables.put("${package}", packageValue);
		variables.put("${path}", packagePath);
		List<ColumnMetadata> columnMetas = getMetaData(table);

		if (!CollectionUtils.isEmpty(columnMetas)) {
			List<Map<String, String>> getterSetterList = new ArrayList<Map<String, String>>();
			for (ColumnMetadata meta : columnMetas) {
				if (needFilter(meta.getColumnName())) {
					continue;
				}
				
				String fieldName = makeCamelCase(meta.getColumnName(), false);
				String type = getType(meta.getColumnTypeName());
				detail.append("\tprivate " + type + " " + fieldName + ";\n");
				
				Map<String, String> getterSetterVariables = new HashMap<String, String>();
				getterSetterVariables.put(
						"${fieldWithUpperFirstChar}",
						Character.toUpperCase(fieldName.charAt(0))
								+ fieldName.substring(1, fieldName.length()));
				getterSetterVariables.put("${type}", type);
				getterSetterVariables.put("${field}", fieldName);
				getterSetterVariables.put("${column}", meta.getColumnName());
				getterSetterList.add(getterSetterVariables);
			}

			String getterSetterTemplate = getTemplate("getter_setter_template");
			if (!CollectionUtils.isEmpty(getterSetterList)) {
				for (Map<String, String> map : getterSetterList) {
					detail.append(replaceAllVariables(getterSetterTemplate, map) + '\n');
				}
			}

			variables.put("${detail}", detail.toString());
		}
		// domain
		String domainTemplate = replaceAllVariables(getTemplate("domain_template"), variables);
		String domainJavaFilePath = replaceAllVariables(javaFilePrefix
				+ "/${path}/domain/${domainClass}.java", variables);
		
		writeToFile(domainTemplate, domainJavaFilePath, replaceDomainIfExist);

		// dao
		String daoTemplate = replaceAllVariables(getTemplate("dao_template"), variables);
		String daoJavaFilePath = replaceAllVariables(javaFilePrefix + "/${path}/dao/${domainClass}Dao.java",
				variables);
		;
		writeToFile(daoTemplate, daoJavaFilePath, replaceDaoIfExist);

		// dao impl
		String daoImplTemplate = replaceAllVariables(getTemplate("daoimpl_template"), variables);
		String daoImplJavaFilePath = replaceAllVariables(javaFilePrefix
				+ "/${path}/dao/sql/${domainClass}DaoSqlImpl.java", variables);
		;
		writeToFile(daoImplTemplate, daoImplJavaFilePath, replaceDaoIfExist);

		// service
		String serviceTemplate = replaceAllVariables(getTemplate("service_template"), variables);
		String serviceJavaFilePath = replaceAllVariables(javaFilePrefix
				+ "/${path}/service/${domainClass}Service.java", variables);
		;
		writeToFile(serviceTemplate, serviceJavaFilePath, replaceServiceIfExist);

		// service impl
		String serviceImplTemplate = replaceAllVariables(getTemplate("serviceimpl_template"), variables);
		String serviceImplJavaFilePath = replaceAllVariables(javaFilePrefix
				+ "/${path}/service/impl/${domainClass}ServiceImpl.java", variables);
		writeToFile(serviceImplTemplate, serviceImplJavaFilePath, replaceServiceIfExist);
	}

	private void writeToFile(String template, String javaFilePath, boolean replace) throws IOException {
		File file = new File(javaFilePath);
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			System.out.println("write file :" + javaFilePath);
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream os = new BufferedOutputStream(fos);
			os.write(template.getBytes("UTF-8"), 0, template.length());
			os.flush();
			fos.close();
			os.close();
		} else if (replace) {
			System.out.println("replace file :" + javaFilePath);
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream os = new BufferedOutputStream(fos);
			os.write(template.getBytes("UTF-8"), 0, template.length());
			os.flush();
			fos.close();
			os.close();
		}
	}
	private static List<String> getAllVariables(String template) {
		List<String> results = Lists.newArrayList();
		if (StringUtils.isBlank(template)) {
			return Lists.newArrayList();
		}
		Pattern variablePattern = Pattern.compile("(\\$\\{.*?\\})");
		Matcher matcher = variablePattern.matcher(template);
		while (matcher.find()) {
			results.add(matcher.group(1));
		}
		return results;
	}

	public String replaceAllVariables(String template, Map<String, String> map) {
		List<String> variables = getAllVariables(template);
		if (!CollectionUtils.isEmpty(variables)) {
			for (String var : variables) {
				if (!map.containsKey(var)) {
					throw new RuntimeException("template contain " + var + ", but not find in map");
				}
			}
		}

		String content = template;
		for (String var : variables) {
			content = content.replace(var, map.get(var));
		}

		return content;

	}

	public String getTemplate(String ftl) {
		String template;
		try {
			StringBuilder sb = new StringBuilder(384);
			BufferedReader reader = new BufferedReader(new InputStreamReader(applicationContext.getResource(
					"classpath:/reverse_template/" + ftl).getInputStream(), "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + '\n');
			}
			template = sb.toString();
			IOUtils.closeQuietly(reader);
		} catch (IOException e) {
			template = "";
			e.printStackTrace();
		}
		return template;
	}

	private String getType(String typeName) {
		if ("TEXT".equalsIgnoreCase(typeName)) {
			return "String";
		}
		if ("VARCHAR".equalsIgnoreCase(typeName)) {
			return "String";
		}
		if ("BIGINT".equalsIgnoreCase(typeName)) {
			return "Long";
		}
		
		if ("INT".equalsIgnoreCase(typeName)) {
			return "Integer";
		}
		
		if ("SMALLINT".equalsIgnoreCase(typeName)) {
			return "Integer";
		}

		if ("TINYINT".equalsIgnoreCase(typeName)) {
			return "Integer";
		}
		
		if ("DECIMAL".equalsIgnoreCase(typeName)) {
			return "BigDecimal";
		}

		return "String";
	}

	public String makeCamelCase(String value, boolean firstToUpperCase) {
		if (!StringUtils.isBlank(value)) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < value.length(); i++) {
				if (i == 0 && firstToUpperCase) {
					sb.append(Character.toUpperCase(value.charAt(i)));
					continue;
				}

				if (value.charAt(i) == '_') {
					char ch = 0;
					while (i < value.length() && value.charAt(i) == '_') {
						i++;
						ch = Character.toUpperCase(value.charAt(i));
					}
					sb.append(ch);
				} else {
					sb.append(value.charAt(i));
				}
			}
			return sb.toString();
		}
		return value;
	}

}

