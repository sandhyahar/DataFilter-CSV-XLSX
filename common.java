package com.API.Common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.API.pojo.FilterCondition;
import com.API.pojo.FilterDataMaster;
import com.ibm.icu.text.SimpleDateFormat;
import com.monitorjbl.xlsx.StreamingReader;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;

public class common {
	public static boolean write(MultipartFile file, String filePath) {
		boolean flag = false;

		try {

			Path filepath = Paths.get(filePath);

			try (OutputStream os = Files.newOutputStream(filepath)) {
				os.write(file.getBytes());

			}
			flag = true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return flag;
	}

	public static boolean readAndWriteExcelFile(MultipartFile filedata, String filePath) {
		ArrayList<String> sheetNames = new ArrayList<>();
		ArrayList<Integer> sheetIndices = new ArrayList<>();
		boolean flag = true;
		InputStream myxls = null;

		// Read Workbook
		Workbook readWB = null;
		Sheet readSheet = null;
		Row readRow = null;

		// Write Workbook
		SXSSFWorkbook writeWB = null;
		Sheet writeSheet = null;
		Row writeRow = null;
		Cell writeCell = null;

		try {
			myxls = ((MultipartFile) filedata).getInputStream();
			readWB = StreamingReader.builder().rowCacheSize(100) 
					.bufferSize(1024) 
					.open(myxls);

			int noOfSheet = readWB.getNumberOfSheets();
			writeWB = new SXSSFWorkbook();

			for (int sheetCnt = 0; sheetCnt < noOfSheet; sheetCnt++) {
				readSheet = readWB.getSheetAt(sheetCnt);
				int totalRow = readSheet.getLastRowNum();

				if (totalRow > 0) {
					sheetNames.add(readWB.getSheetName(sheetCnt));
					sheetIndices.add(sheetCnt);
					writeSheet = writeWB.createSheet(readWB.getSheetName(sheetCnt));
					writeSheet.setColumnWidth(0, 5000);
				}

				int rowCnt = 0;

				for (Row row : readSheet) {
					if (row != null) {
						writeRow = writeSheet.createRow(rowCnt);
						readRowExcel(row, writeSheet, writeRow, rowCnt);
						rowCnt++;

					}
				}
			}

			flag = writeXLS(writeWB, filePath);

			writeSheet = null;
			writeWB.close();
			readWB.close();
			writeWB = null;
		} catch (Exception e) {
			flag = false;
			return false;
		}

		return true;
	}

	private static boolean readRowExcel(Row readRow, Sheet writeSheet, Row writeRow, int rowSetPosition) {
		boolean flag = true;
		String cellString ;
		try {
			for (int colCnt = 0; colCnt < readRow.getLastCellNum(); colCnt++) {
				Cell writeCell = writeRow.createCell(colCnt);
				cellString = getExcelCellValue(readRow.getCell(colCnt));
				writeCell.setCellValue(cellString);
			}
			
		} catch (Exception e) {
			flag = false;
		}

		return flag;
	}

	private static String getExcelCellValue(Cell cell) {
	    if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA && cell != null) {
	        if (DateUtil.isCellDateFormatted(cell)) {
	            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	            return dateFormat.format(cell.getDateCellValue());
	        } else {
	            double numericValue = cell.getNumericCellValue();
	            long originalValue = (long) numericValue;
	            return String.valueOf(originalValue);
	        }
	    } else if (cell.getCellType() == CellType.STRING) {
	        return cell.getStringCellValue();
	    } else {
	        return cell.toString();
	    }
	}


	private static boolean writeXLS(Workbook writeWB, String filePath) {
		boolean flag = true;

		try {
			FileOutputStream stream = new FileOutputStream(new File(filePath));
			writeWB.write(stream);
			stream.close();
		} catch (Exception e) {
			flag = false;
		}

		return flag;
	}

	public static String encryptStringAdvance(String plainText) {
		String strCipherText = "";
		try {
			plainText = plainText + "h@y^Iu$trN";
			byte[] defaultBytes = plainText.getBytes();
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(defaultBytes);
			byte[] messageDigest = algorithm.digest();
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String hex = Integer.toHexString(0xFF & messageDigest[i]);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			strCipherText = hexString.substring(0);
		} catch (Exception ex) {
			// Smslog.debug("Probem in Common.encryptString");
			// Smslog.error(ex);
			System.out.println(ex);

		}
		return strCipherText;
	}

	public static String removeSpacesAndSpecialCharacters(String input) {
		String result = input.replaceAll("[^a-zA-Z0-9]", "");

		return result;
	}

	public static String mobileValidation(String name) {
		// Remove all non-digit characters from the mobile number
		String cleanedMobileNumber = name.replaceAll("\\D", "");
		if (cleanedMobileNumber.length() == 10 || cleanedMobileNumber.length() == 12) {
			return cleanedMobileNumber;
		} else {
			return "";
		}
	}

	public static String emailValidation(String email) {
		String sanitizedEmail = email.replaceAll("[+_\\/\\s]", "");
		// Validate the email format using a regular expression
		String regex = "^[a-zA-Z0-9.!#$%&'*+-/=?^_`{|}~]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		if (sanitizedEmail.matches(regex)) {
			return sanitizedEmail; // Return the sanitized email if it's valid
		} else {
			return ""; // Return an empty string for invalid
		}
	}

	public static String pincodeValidation(String pincode) {
		// Remove all characters that are not digits or '-'
		pincode = pincode.replaceAll("[^0-9-]", "");
		pincode = pincode.replace(" ", "");
		pincode = pincode.trim();

		int length = pincode.length();
		if (length >= 5 && length <= 11 && pincode.matches("^[0-9-]{5,11}$")) {
			return pincode;
		}

		return "";
	}

//	public static JSONObject getJson(String input) {
//		JSONObject json = new JSONObject();
//		try {
//			// Split the input string by commas
//			// String[] keyValuePairs = input.split(", ");
//			String[] keyValuePairs = input.split(",\\s*(?![^()]*\\))=");
//
//			// Process each key-value pair and add it to the JSONObject
//			for (String pair : keyValuePairs) {
//				// Split the pair into key and value
//				String[] keyValue = pair.split("=");
//
//				// Make sure the pair is valid
//				if (keyValue.length == 2) {
//					String key = keyValue[0].trim();
//					String value = keyValue[1].trim();
//
//					// Add the key-value pair to the JSONObject
//					json.put(key, value);
//				} else if (keyValue.length == 1) {
//					String key = keyValue[0].trim();
//
//					// Add the key with an empty string value to the JSONObject
//					json.put(key, "");
//				} else {
//					System.out.println("Invalid pair: " + pair);
//				}
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return json;
//	}

	public static List<Query> prepareQueryList(FilterDataMaster filterDataMaster) {
		List<Query> queryList = new ArrayList<>();

		Map<String, String> conditionMap = new HashMap<>();
		conditionMap.put("dataMstId.keyword", filterDataMaster.getDataMstId());
		conditionMap.put("userId.keyword", filterDataMaster.getUserId());

		queryList = conditionMap.entrySet().stream().filter(entry -> !ObjectUtils.isEmpty(entry.getValue()))
				.map(entry -> QueryBuilderUtils.termQuery(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());

		getFilterListData(queryList, filterDataMaster.getFilterList());

		return queryList;
	}

	public static void getFilterListData(List<Query> queryList, List<FilterCondition> filterList) {
		for (FilterCondition filterObject : filterList) {
			String field = filterObject.getField();
			String condition = filterObject.getCondition();
			String value = filterObject.getValue();

			if (condition.equalsIgnoreCase("equalvalue")) {
				queryList.add(QueryBuilderUtils.termQuery(field, value));
			} else if (condition.equalsIgnoreCase("contains")) {
				queryList.add(QueryBuilderUtils.matchQuery(field, value));
			} else {
				queryList.add(QueryBuilderUtils.termQuery(field, value));
			}
		}
	}

}
