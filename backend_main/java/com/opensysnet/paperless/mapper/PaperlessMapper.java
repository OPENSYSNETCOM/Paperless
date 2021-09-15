package com.opensysnet.paperless.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.opensysnet.paperless.model.InstallParam;
import com.opensysnet.paperless.model.ServiceParam;
import org.springframework.stereotype.Repository;


@Repository
public interface PaperlessMapper {

	LinkedHashMap<String, Object> selectUserInfo(HashMap<String, Object> param);

	List<LinkedHashMap<String, Object>> selectStoreInfo(HashMap<String, Object> param);

	Integer selectLastStoreSeq();

	List<LinkedHashMap<String, Object>> selectStoreInfoAll(HashMap<String, Object> param);

	Integer insertStorePositionInfo(HashMap<String, Object> param);

	List<LinkedHashMap<String, Object>> selectScheduleInfo(HashMap<String, Object> param);

	List<LinkedHashMap<String, Object>> selectInstallWorkingInfo(HashMap<String, Object> param);

	List<LinkedHashMap<String, Object>>	selectServiceWorkingInfo(HashMap<String, Object> param);

	List<LinkedHashMap<String, Object>> selectInstallDeviceInfo();

	Integer insertInstallInfo(HashMap<String, Object> param);

	Integer insertInstallDeviceModelInfo(List<InstallParam.DeviceInfo> dev_list);

	Integer insertInstallSamInfo(HashMap<String, Object> param);

	Integer insertInstallCheckInfo(HashMap<String, Object> param);

	Integer insertInstallDongleInfo(List<InstallParam.DongleInfo> dongle_list);

	LinkedHashMap<String, Object> selectInstallInfo(HashMap<String, Object> param);

	List<LinkedHashMap<String, Object>> selectInstallDeviceModelInfo(HashMap<String, Object> param);

	LinkedHashMap<String, Object> selectInstallSamInfo(HashMap<String, Object> param);

	LinkedHashMap<String, Object> selectInstallCheckInfo(HashMap<String, Object> param);

	List<LinkedHashMap<String, Object>> selectInstallDongleInfo(HashMap<String, Object> param);

	Integer modifyInstallInfo(HashMap<String, Object> param);

	Integer modifyInstallDeviceModelInfo(HashMap<String, Object> param);

	Integer modifyInstallSamInfo(HashMap<String, Object> param);

	Integer modifyInstallCheckInfo(HashMap<String, Object> param);

	Integer modifyInstallDongleInfo(HashMap<String, Object> param);

	LinkedHashMap<String, Object> selectInstallSignInfo(HashMap<String, Object> param);

	Integer deleteInstallInfo(HashMap<String, Object> param);

	Integer deleteInstallDeviceModelInfo(HashMap<String, Object> param);

	Integer deleteInstallSamInfo(HashMap<String, Object> param);

	Integer deleteInstallCheckInfo(HashMap<String, Object> param);

	Integer deleteInstallDongleInfo(HashMap<String, Object> param);

	List<LinkedHashMap<String, Object>> selectServiceDeviceInfo();

	List<LinkedHashMap<String, Object>> selectServiceCheckInfo();

	Integer insertServiceInfo(HashMap<String, Object> param);

	Integer insertServiceDeviceModelInfo(List<ServiceParam.DeviceInfo> dev_list);

	Integer insertServiceFixInfo(List<ServiceParam.FixInfo> fix_list);

	Integer insertServicePartInfo(List<ServiceParam.PartInfo> part_list);

	Integer modifyServiceInfo(HashMap<String, Object> param);

	Integer deleteServiceDeviceModelInfo(HashMap<String, Object> param);

	Integer deleteServiceFixInfo(HashMap<String, Object> param);

	Integer deleteServicePartInfo(HashMap<String, Object> param);

	Integer deleteServiceInfo(HashMap<String, Object> param);

	LinkedHashMap<String, Object> selectServiceSignInfo(HashMap<String, Object> param);

	LinkedHashMap<String, Object> selectServiceInfo(HashMap<String, Object> param);

}
