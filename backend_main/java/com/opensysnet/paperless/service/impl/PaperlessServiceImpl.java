package com.opensysnet.paperless.service.impl;

import java.util.*;

import ch.qos.logback.core.util.FileUtil;
import com.opensysnet.paperless.common.utils.FileUtils;
import com.opensysnet.paperless.model.InstallParam;
import com.opensysnet.paperless.model.ServiceParam;
import org.apache.tomcat.jni.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opensysnet.paperless.mapper.PaperlessMapper;
import com.opensysnet.paperless.service.PaperlessService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import sun.awt.image.ImageWatched;

@Service
@Transactional
@Slf4j
public class PaperlessServiceImpl implements PaperlessService {
	@Autowired
    private PaperlessMapper paperlessMapper;

	@Override
	public LinkedHashMap<String, Object> processLogin(HashMap<String, Object> param) {
		log.debug("************************ processLogin service start ************************");
		LinkedHashMap<String, Object> user_info = new LinkedHashMap<>();
		try {
			user_info = paperlessMapper.selectUserInfo(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("************************ processLogin service end ************************");
		return user_info;
	}

	@Override
	public List<LinkedHashMap<String, Object>> processGetStoreList(HashMap<String, Object> param) {
		log.debug("************************ processGetStoreList service start ************************");
		List<LinkedHashMap<String, Object>> store_list = new ArrayList<>();
		try {
			Double lat = Double.parseDouble((String)param.get("latitude"));
			Double lon = Double.parseDouble((String)param.get("longitude"));
			Double scale = (int)param.get("scale") / (double)100000;

			param.put("lat_min", lat-scale);
			param.put("lat_max", lat+scale);
			param.put("lon_min", lon-scale);
			param.put("lon_max", lon+scale);

			store_list = paperlessMapper.selectStoreInfo(param);

		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("************************ processGetStoreList service end ************************");
		return store_list;
	}

	@Override
	public List<LinkedHashMap<String, Object>> processGetScheduleList(HashMap<String, Object> param) {
		log.debug("************************ processGetScheduleList service start ************************");
		List<LinkedHashMap<String, Object>> schedule_list = new ArrayList<>();
		try {
			schedule_list = paperlessMapper.selectScheduleInfo(param);
			for (LinkedHashMap<String, Object> schedule_info : schedule_list) {
				param.put("seq", schedule_info.get("seq"));
				LinkedHashMap<String, Object> work_info = paperlessMapper.selectInstallInfo(param);
				if (work_info != null) schedule_info.put("paperless_flag", "Y");
				//work_info = paperlessMapper.selectServiceInfo(param);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("************************ processGetScheduleList service end ************************");
		return schedule_list;
	}

	@Override
	public List<LinkedHashMap<String, Object>> processGetWorkingList(HashMap<String, Object> param) {
		log.debug("************************ processGetWorkingList service start ************************");
		List<LinkedHashMap<String, Object>> working_list = new ArrayList<>();
		try {

			if (param.get("type").equals("INSTALL"))
				working_list = paperlessMapper.selectInstallWorkingInfo(param);
			else
				working_list = paperlessMapper.selectServiceWorkingInfo(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("************************ processGetWorkingList service end ************************");
		return working_list;
	}

    @Override
    public List<LinkedHashMap<String, Object>> processGetInstallDeviceList() {
        log.debug("************************ processGetWorkingList service start ************************");
        List<LinkedHashMap<String, Object>> device_list = new ArrayList<>();
        try {
            device_list = paperlessMapper.selectInstallDeviceInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("************************ processGetWorkingList service end ************************");
        return device_list;
    }

    @Override
	public Integer processRegInstallInfo(HashMap<String, Object> param) {
		log.debug("************************ processRegInstallInfo service start ************************");
		Integer result = 0;
		try {
			result = paperlessMapper.insertInstallInfo(param);

			if ( result == 1) {
				List<InstallParam.DeviceInfo> dev_list = (List)param.get("dev_list");
				for ( InstallParam.DeviceInfo dev_info : dev_list) {
					dev_info.setSeq((int)param.get("seq"));
				}
				paperlessMapper.insertInstallDeviceModelInfo(dev_list);

				InstallParam.SamInfo sam_info = (InstallParam.SamInfo)param.get("sam_info");
				HashMap<String, Object> sam_param = new HashMap<>();
				sam_param.put("seq", param.get("seq"));
				sam_param.put("tmoney_1", sam_info.getTmoney_1());
				sam_param.put("tmoney_2", sam_info.getTmoney_2());
				sam_param.put("intsam_1", sam_info.getIntsam_1());
				sam_param.put("intsam_2", sam_info.getIntsam_2());

				paperlessMapper.insertInstallSamInfo(sam_param);

				InstallParam.CheckInfo check_info = (InstallParam.CheckInfo)param.get("check_info");
				HashMap<String, Object> check_param = new HashMap<>();
				check_param.put("seq", param.get("seq"));
				check_param.put("pos_num_check_flag", check_info.getPos_num_check_flag());
				check_param.put("pos_num_val", check_info.getPos_num_val());
				check_param.put("pos_ver_check_flag", check_info.getPos_ver_check_flag());
				check_param.put("pos_ver_val", check_info.getPos_ver_val());
				check_param.put("openday", check_info.getOpenday());
				check_param.put("startday", check_info.getStartday());
				check_param.put("got_check_flag", check_info.getGot_check_flag());
				check_param.put("got_val", check_info.getGot_val());
				check_param.put("internet_flag", check_info.getInternet_flag());
				check_param.put("internet_val", check_info.getInternet_val());
				check_param.put("sc_pgm_flag", check_info.getSc_pgm_flag());
				check_param.put("sc_pgm_val", check_info.getSc_pgm_val());
				check_param.put("pos_cash_flag", check_info.getPos_cash_flag());
				check_param.put("pos_cash_val", check_info.getPos_cash_val());
				check_param.put("pos_credit_flag", check_info.getPos_credit_flag());
				check_param.put("pos_credit_val", check_info.getPos_credit_val());
				check_param.put("pos_point_flag", check_info.getPos_point_flag());
				check_param.put("pos_point_val", check_info.getPos_point_val());
				check_param.put("sc_work_flag", check_info.getSc_work_flag());
				check_param.put("sc_work_val", check_info.getSc_work_val());
				check_param.put("got_update_flag", check_info.getGot_update_flag());
				check_param.put("got_update_val", check_info.getGot_update_val());

				paperlessMapper.insertInstallCheckInfo(check_param);

				List<InstallParam.DongleInfo> dongle_list = (List)param.get("dongle_list");
				for ( InstallParam.DongleInfo dongle_info : dongle_list) {
					dongle_info.setSeq((int)param.get("seq"));
				}
				paperlessMapper.insertInstallDongleInfo(dongle_list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("************************ processRegInstallInfo service end ************************");
		return result;
	}

	@Override
	public String uploadSignFile (MultipartFile sign_file, HashMap<String, Object> param, String type) {
		log.debug("************************ uploadSignFile service start ************************");
		String url = "";
		try {
			String file_id = "";
			if (type.equals("INSTALL"))
				file_id = "install_sign_" + param.get("seq") + "_" + param.get("type");
			else if (type.equals("SERVICE"))
				file_id = "service_sign_" + param.get("seq") + "_" + param.get("type");
			String filename = FileUtils.uploadFiles(sign_file, file_id);
			url = FileUtils.getUploadUrl() + filename;
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("************************ uploadSignFile service end ************************");
		return url;
	}

	@Override
	public LinkedHashMap<String, Object> processGetInstallInfo(HashMap<String, Object> param) {
		log.debug("************************ processGetInstallInfo service start ************************");
		LinkedHashMap<String, Object> install_info = new LinkedHashMap<>();
		try {
			install_info = paperlessMapper.selectInstallInfo(param);
			List<LinkedHashMap<String, Object>> dev_list = paperlessMapper.selectInstallDeviceModelInfo(param);
			if (dev_list.size() > 0) install_info.put("dev_list", dev_list);
			LinkedHashMap<String, Object> sam_info = paperlessMapper.selectInstallSamInfo(param);
			if (sam_info != null) install_info.put("sam_info", sam_info);
			LinkedHashMap<String, Object> check_info = paperlessMapper.selectInstallCheckInfo(param);
			if (check_info != null) install_info.put("check_info", check_info);
			List<LinkedHashMap<String, Object>> dongle_list = paperlessMapper.selectInstallDongleInfo(param);
			if (dongle_list.size() > 0 ) install_info.put("dongle_list", dongle_list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("************************ processGetInstallInfo service end ************************");
		return install_info;
	}

	@Override
	public Integer processModifyInstallInfo(Boolean basic_flag, HashMap<String, Object> param) {
		log.debug("************************ processModifyInstallInfo service start ************************");
		Integer result = 0;
		try {
			if ( basic_flag == true )
				result = paperlessMapper.modifyInstallInfo(param);

			if (param.containsKey("dev_list")) {
				paperlessMapper.deleteInstallDeviceModelInfo(param);
				List<InstallParam.DeviceInfo> dev_list = (List)param.get("dev_list");
				for ( InstallParam.DeviceInfo dev_info : dev_list) {
					dev_info.setSeq((int)param.get("seq"));
				}
				paperlessMapper.insertInstallDeviceModelInfo(dev_list);
			}

			if (param.containsKey("sam_info")) {
				paperlessMapper.deleteInstallSamInfo(param);
				InstallParam.SamInfo sam_info = (InstallParam.SamInfo) param.get("sam_info");
				HashMap<String, Object> sam_param = new HashMap<>();
				sam_param.put("seq", param.get("seq"));
				sam_param.put("tmoney_1", sam_info.getTmoney_1());
				sam_param.put("tmoney_2", sam_info.getTmoney_2());
				sam_param.put("intsam_1", sam_info.getIntsam_1());
				sam_param.put("intsam_2", sam_info.getIntsam_2());

				paperlessMapper.insertInstallSamInfo(sam_param);
			}

			if ( param.containsKey("check_info")) {
				paperlessMapper.deleteInstallCheckInfo(param);
				InstallParam.CheckInfo check_info = (InstallParam.CheckInfo) param.get("check_info");
				HashMap<String, Object> check_param = new HashMap<>();
				check_param.put("seq", param.get("seq"));
				check_param.put("pos_num_check_flag", check_info.getPos_num_check_flag());
				check_param.put("pos_num_val", check_info.getPos_num_val());
				check_param.put("pos_ver_check_flag", check_info.getPos_ver_check_flag());
				check_param.put("pos_ver_val", check_info.getPos_ver_val());
				check_param.put("openday", check_info.getOpenday());
				check_param.put("startday", check_info.getStartday());
				check_param.put("got_check_flag", check_info.getGot_check_flag());
				check_param.put("got_val", check_info.getGot_val());
				check_param.put("internet_flag", check_info.getInternet_flag());
				check_param.put("internet_val", check_info.getInternet_val());
				check_param.put("sc_pgm_flag", check_info.getSc_pgm_flag());
				check_param.put("sc_pgm_val", check_info.getSc_pgm_val());
				check_param.put("pos_cash_flag", check_info.getPos_cash_flag());
				check_param.put("pos_cash_val", check_info.getPos_cash_val());
				check_param.put("pos_credit_flag", check_info.getPos_credit_flag());
				check_param.put("pos_credit_val", check_info.getPos_credit_val());
				check_param.put("pos_point_flag", check_info.getPos_point_flag());
				check_param.put("pos_point_val", check_info.getPos_point_val());
				check_param.put("sc_work_flag", check_info.getSc_work_flag());
				check_param.put("sc_work_val", check_info.getSc_work_val());
				check_param.put("got_update_flag", check_info.getGot_update_flag());
				check_param.put("got_update_val", check_info.getGot_update_val());

				paperlessMapper.insertInstallCheckInfo(check_param);
			}

			if ( param.containsKey("dongle_list")) {
				paperlessMapper.deleteInstallDongleInfo(param);
				List<InstallParam.DongleInfo> dongle_list = (List)param.get("dongle_list");
				for ( InstallParam.DongleInfo dongle_info : dongle_list) {
					dongle_info.setSeq((int)param.get("seq"));
				}
				paperlessMapper.insertInstallDongleInfo(dongle_list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("************************ processModifyInstallInfo service end ************************");
		return result;
	}

	@Override
	public Integer processDeleteInstallInfo(HashMap<String, Object> param) {
		log.debug("************************ processDeleteInstallInfo service start ************************");
		Integer result = 0;
		try {
			LinkedHashMap<String, Object> sign_info = paperlessMapper.selectInstallSignInfo(param);
			String url = "";
			if (sign_info != null ) {
				if (sign_info.containsKey("owner_sign")) {
					url = (String) sign_info.get("owner_sign");
					String filename = url.substring(url.lastIndexOf('/') + 1, url.length());
					FileUtils.deleteFiles(filename);
				}
				if (sign_info.containsKey("oper_sign")) {
					url = (String) sign_info.get("oper_sign");
					String filename = url.substring(url.lastIndexOf('/') + 1, url.length());
					FileUtils.deleteFiles(filename);
				}
				if (sign_info.containsKey("comp_sign")) {
					url = (String) sign_info.get("comp_sign");
					String filename = url.substring(url.lastIndexOf('/') + 1, url.length());
					FileUtils.deleteFiles(filename);
				}
				if (sign_info.containsKey("tech_sign")) {
					url = (String) sign_info.get("tech_sign");
					String filename = url.substring(url.lastIndexOf('/') + 1, url.length());
					FileUtils.deleteFiles(filename);
				}
			}

			paperlessMapper.deleteInstallDongleInfo(param);
			paperlessMapper.deleteInstallCheckInfo(param);
			paperlessMapper.deleteInstallSamInfo(param);
			paperlessMapper.deleteInstallDeviceModelInfo(param);
			result = paperlessMapper.deleteInstallInfo(param);

		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("************************ processDeleteInstallInfo service end ************************");
		return result;
	}

	@Override
	public List<LinkedHashMap<String, Object>> processGetServiceDeviceList() {
		log.debug("************************ processGetServiceDeviceList service start ************************");
		List<LinkedHashMap<String, Object>> device_list = new ArrayList<>();
		try {
			device_list = paperlessMapper.selectServiceDeviceInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("************************ processGetServiceDeviceList service end ************************");
		return device_list;
	}

	@Override
	public List<LinkedHashMap<String, Object>> processGetServiceCheckList() {
		log.debug("************************ processGetServiceCheckList service start ************************");
		List<LinkedHashMap<String, Object>> check_list = new ArrayList<>();
		try {
			check_list = paperlessMapper.selectServiceCheckInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("************************ processGetServiceCheckList service end ************************");
		return check_list;
	}

	@Override
	public Integer processRegServiceInfo(HashMap<String, Object> param) {
		log.debug("************************ processRegServiceInfo service start ************************");
		Integer result = 0;
		try {
			result = paperlessMapper.insertServiceInfo(param);
			if (result == 1) {
				if (param.containsKey("dev_list")) {
					List<ServiceParam.DeviceInfo> dev_list = (List)param.get("dev_list");
					for ( ServiceParam.DeviceInfo dev_info : dev_list) {
						dev_info.setSeq((int)param.get("seq"));
					}
					paperlessMapper.insertServiceDeviceModelInfo(dev_list);
				}

				if (param.containsKey("fix_list")) {
					List<ServiceParam.FixInfo> fix_list = (List)param.get("fix_list");
					for ( ServiceParam.FixInfo fix_info : fix_list) {
						fix_info.setSeq((int)param.get("seq"));
					}
					paperlessMapper.insertServiceFixInfo(fix_list);
				}

				if (param.containsKey("part_list")) {
					List<ServiceParam.PartInfo> part_list = (List)param.get("part_list");
					for ( ServiceParam.PartInfo part_info : part_list) {
						part_info.setSeq((int)param.get("seq"));
					}
					paperlessMapper.insertServicePartInfo(part_list);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("************************ processRegServiceInfo service end ************************");
		return result;
	}

	@Override
	public Integer processModifyServiceInfo(Boolean basic_flag, HashMap<String, Object> param) {
		log.debug("************************ processModifyServiceInfo service start ************************");
		Integer result = 0;
		try {
			if ( basic_flag == true )
				result = paperlessMapper.modifyServiceInfo(param);

			if (param.containsKey("dev_list")) {
				paperlessMapper.deleteServiceDeviceModelInfo(param);
				List<ServiceParam.DeviceInfo> dev_list = (List)param.get("dev_list");
				for ( ServiceParam.DeviceInfo dev_info : dev_list) {
					dev_info.setSeq((int)param.get("seq"));
				}
				result = paperlessMapper.insertServiceDeviceModelInfo(dev_list);
			}

			if (param.containsKey("fix_list")) {
				paperlessMapper.deleteServiceFixInfo(param);
				List<ServiceParam.FixInfo> fix_list= (List) param.get("fix_list");
				for ( ServiceParam.FixInfo fix_info : fix_list ) {
					fix_info.setSeq((int)param.get("seq"));
				}
				result = paperlessMapper.insertServiceFixInfo(fix_list);

			}

			if ( param.containsKey("part_list")) {
				paperlessMapper.deleteServicePartInfo(param);
				List<ServiceParam.PartInfo> part_list = (List)param.get("part_list");
				for ( ServiceParam.PartInfo part_info : part_list) {
					part_info.setSeq((int)param.get("seq"));
				}
				result = paperlessMapper.insertServicePartInfo(part_list);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("************************ processModifyServiceInfo service end ************************");
		return result;
	}

	@Override
	public Integer processDeleteServiceInfo(HashMap<String, Object> param) {
		log.debug("************************ processDeleteServiceInfo service start ************************");
		Integer result = 0;
		try {
			LinkedHashMap<String, Object> sign_info = paperlessMapper.selectServiceSignInfo(param);
			String url = "";
			if (sign_info != null) {
				if (sign_info.containsKey("owner_sign")) {
					url = (String)sign_info.get("owner_sign");
					String filename =  url.substring(url.lastIndexOf('/') + 1, url.length());
					FileUtils.deleteFiles(filename);
				}
				if (sign_info.containsKey("ce_sign")) {
					url = (String)sign_info.get("ce_sign");
					String filename =  url.substring(url.lastIndexOf('/') + 1, url.length());
					FileUtils.deleteFiles(filename);
				}
			}

			paperlessMapper.deleteServiceDeviceModelInfo(param);
			paperlessMapper.deleteServiceFixInfo(param);
			paperlessMapper.deleteServicePartInfo(param);
			result = paperlessMapper.deleteServiceInfo(param);

		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("************************ processDeleteServiceInfo service end ************************");
		return result;
	}

	@Override
	public LinkedHashMap<String, Object> processGetServiceInfo(HashMap<String, Object> param) {
		log.debug("************************ processGetServiceInfo service start ************************");
		LinkedHashMap<String, Object> service_info = new LinkedHashMap<>();
		try {
			service_info = paperlessMapper.selectServiceInfo(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("************************ processGetServiceInfo service end ************************");
		return service_info;
	}
}
