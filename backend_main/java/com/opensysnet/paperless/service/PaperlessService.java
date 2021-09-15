package com.opensysnet.paperless.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface PaperlessService {
	LinkedHashMap<String, Object> processLogin(HashMap<String, Object> param);

	List<LinkedHashMap<String, Object>> processGetStoreList(HashMap<String, Object> param);

	List<LinkedHashMap<String, Object>> processGetScheduleList(HashMap<String, Object> param);

	List<LinkedHashMap<String, Object>> processGetWorkingList(HashMap<String, Object> param);

	List<LinkedHashMap<String, Object>> processGetInstallDeviceList();

	Integer processRegInstallInfo(HashMap<String, Object> param);

	String uploadSignFile (MultipartFile sign_file, HashMap<String, Object> param, String type);

    LinkedHashMap<String, Object> processGetInstallInfo(HashMap<String, Object> param);

    Integer processModifyInstallInfo(Boolean basic_flag, HashMap<String, Object> param);

    Integer processDeleteInstallInfo(HashMap<String, Object> param);

	List<LinkedHashMap<String, Object>> processGetServiceDeviceList();

	List<LinkedHashMap<String, Object>> processGetServiceCheckList();

	Integer processRegServiceInfo(HashMap<String, Object> param);

	Integer processModifyServiceInfo(Boolean basic_flag, HashMap<String, Object> param);

	Integer processDeleteServiceInfo(HashMap<String, Object> param);

	LinkedHashMap<String, Object> processGetServiceInfo(HashMap<String, Object> param);

}
