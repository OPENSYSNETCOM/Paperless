package com.opensysnet.paperless.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opensysnet.paperless.common.model.ExcelModel;
import com.opensysnet.paperless.model.InstallParam;
import com.opensysnet.paperless.model.ReqParam;
import com.opensysnet.paperless.model.ServiceParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.opensysnet.paperless.dto.ResultCode;
import com.opensysnet.paperless.service.PaperlessService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class PaperlessController extends AbstractRestController {

    @Autowired
    private PaperlessService paperlessService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    LinkedHashMap<String, Object> processLogin(
            @RequestBody HashMap<String, Object> reqMap) {
    	log.debug("************************ processLogin Controller start ************************");
        Gson gson = new Gson();
        String reqStr = gson.toJson(reqMap.get("req_param"));
        ReqParam reqParam = gson.fromJson(reqStr, new TypeToken<ReqParam>(){}.getType());
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
        	HashMap<String, Object> param = new HashMap<>();
            if (reqParam.getUser_id() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist USER_ID");
                return response;
            }
            param.put("user_id", reqParam.getUser_id());

            if (reqParam.getPasswd() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Password");
                return response;
            }
            param.put("passwd", reqParam.getPasswd());

            LinkedHashMap<String, Object> user_info = paperlessService.processLogin(param);
            if ( user_info == null ) {
                response.put("resultCode",ResultCode.HS1001_NOT_REGISTERED_ID.getCode());
                response.put("resultMessage", "Not Exist User Info");
            } else {
                if (!reqParam.getPasswd().equals(user_info.get("passwd"))) {
                    response.put("resultCode", ResultCode.HS1002_INVALID_PWD.getCode());
                    response.put("resultMessage", "Wrong Password");
                } else {
                    response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
                }
            }
        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processLogin Error : " + e.getMessage(), e);
        }
        log.debug("************************ processLogin Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    LinkedHashMap<String, Object> processLogout(
            @RequestBody HashMap<String, Object> reqMap) {
        log.debug("************************ processLogout Controller start ************************");
        Gson gson = new Gson();
        String reqStr = gson.toJson(reqMap.get("req_param"));
        ReqParam reqParam = gson.fromJson(reqStr, new TypeToken<ReqParam>(){}.getType());
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            if (reqParam.getUser_id() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist USER_ID");
                return response;
            }
            param.put("user_id", reqParam.getUser_id());

            response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());

        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processLogout Error : " + e.getMessage(), e);
        }
        log.debug("************************ processLogout Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/store/list", method = RequestMethod.POST)
    LinkedHashMap<String, Object> processGetStoreList(
            @RequestBody HashMap<String, Object> reqMap) {
        log.debug("************************ processGetStoreList Controller start ************************");
        Gson gson = new Gson();
        String reqStr = gson.toJson(reqMap.get("req_param"));
        ReqParam reqParam = gson.fromJson(reqStr, new TypeToken<ReqParam>(){}.getType());
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            if (reqParam.getLatitude() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Latitude");
                return response;
            }
            param.put("latitude", reqParam.getLatitude());

            if (reqParam.getLongitude() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Longitude");
                return response;
            }
            param.put("longitude", reqParam.getLongitude());

            if (reqParam.getScale() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Scale");
                return response;
            }
            param.put("scale", reqParam.getScale());

            List<LinkedHashMap<String, Object>> store_list = paperlessService.processGetStoreList(param);
            if ( store_list.size() > 0 ) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
                response.put("store_cnt", store_list.size());
                response.put("store_list", store_list);
            } else {
                response.put("resultCode", ResultCode.HS404_NOT_FOUND.getCode());
                response.put("resultMessage", ResultCode.HS404_NOT_FOUND.getDefaultMessage());
            }
        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processGetStoreList Error : " + e.getMessage(), e);
        }
        log.debug("************************ processGetStoreList Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/schedule/list", method = RequestMethod.POST)
    LinkedHashMap<String, Object> processGetScheduleList(
            @RequestBody HashMap<String, Object> reqMap) {
        log.debug("************************ processGetScheduleList Controller start ************************");
        Gson gson = new Gson();
        String reqStr = gson.toJson(reqMap.get("req_param"));
        ReqParam reqParam = gson.fromJson(reqStr, new TypeToken<ReqParam>(){}.getType());
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            if (reqParam.getDate() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Date");
                return response;
            }
            param.put("date", reqParam.getDate());

            List<LinkedHashMap<String, Object>> schedule_list = paperlessService.processGetScheduleList(param);
            if ( schedule_list.size() > 0 ) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
                response.put("schedule_cnt", schedule_list.size());
                response.put("schedule_info", schedule_list);
            } else {
                response.put("resultCode", ResultCode.HS404_NOT_FOUND.getCode());
                response.put("resultMessage", ResultCode.HS404_NOT_FOUND.getDefaultMessage());
            }
        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processGetScheduleList Error : " + e.getMessage(), e);
        }
        log.debug("************************ processGetScheduleList Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/working/list", method = RequestMethod.POST)
    LinkedHashMap<String, Object> processGetWorkingList(
            @RequestBody HashMap<String, Object> reqMap) {
        log.debug("************************ processGetWorkingList Controller start ************************");
        Gson gson = new Gson();
        String reqStr = gson.toJson(reqMap.get("req_param"));
        ReqParam reqParam = gson.fromJson(reqStr, new TypeToken<ReqParam>(){}.getType());
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            if (reqParam.getStartdate() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Start Date");
                return response;
            }
            param.put("startdate", reqParam.getStartdate());

            if (reqParam.getEnddate() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist End Date");
                return response;
            }
            param.put("enddate", reqParam.getEnddate());

            if (reqParam.getType() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Type");
                return response;
            }
            if (reqParam.getType().equals("INSTALL") || reqParam.getType().equals("AS"))
                param.put("type", reqParam.getType());
            else {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Invalid Type");
                return response;
            }

            List<LinkedHashMap<String, Object>> working_list = paperlessService.processGetWorkingList(param);
            if ( working_list.size() > 0 ) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
                response.put("working_cnt", working_list.size());
                response.put("working_info", working_list);
            } else {
                response.put("resultCode", ResultCode.HS404_NOT_FOUND.getCode());
                response.put("resultMessage", ResultCode.HS404_NOT_FOUND.getDefaultMessage());
            }
        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processGetWorkingList Error : " + e.getMessage(), e);
        }
        log.debug("************************ processGetWorkingList Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/install/dev", method = RequestMethod.GET)
    LinkedHashMap<String, Object> processGetInstallDeviceList() {
        log.debug("************************ processGetInstallDeviceList Controller start ************************");
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            List<LinkedHashMap<String, Object>> device_list = paperlessService.processGetInstallDeviceList();
            if ( device_list.size() > 0 ) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
                response.put("device_list", device_list);
            } else {
                response.put("resultCode", ResultCode.HS404_NOT_FOUND.getCode());
                response.put("resultMessage", ResultCode.HS404_NOT_FOUND.getDefaultMessage());
            }
        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processGetInstallDeviceList Error : " + e.getMessage(), e);
        }
        log.debug("************************ processGetInstallDeviceList Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/install/reg", method = RequestMethod.POST)
    LinkedHashMap<String, Object> processRegInstallInfo(
            @RequestBody HashMap<String, Object> reqMap) {
        log.debug("************************ processRegInstallInfo Controller start ************************");
        Gson gson = new Gson();
        String reqStr = gson.toJson(reqMap.get("req_param"));
        InstallParam reqParam = gson.fromJson(reqStr, new TypeToken<InstallParam>(){}.getType());
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            if (reqParam.getSeq() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Seq");
                return response;
            }
            param.put("seq", reqParam.getSeq());

            if (reqParam.getCode() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Code");
                return response;
            }
            param.put("code", reqParam.getCode());

            if (reqParam.getOrgan() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Organ");
                return response;
            }
            param.put("organ", reqParam.getOrgan());

            if (reqParam.getOrgan_type() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Organ Type");
                return response;
            }
            param.put("organ_type", reqParam.getOrgan_type());

            if (reqParam.getOpenday() != null)
                param.put("openday", reqParam.getOpenday());
            if (reqParam.getSetupday() != null)
                param.put("setupday", reqParam.getSetupday());
            if (reqParam.getWork_type() != null)
                param.put("work_type", reqParam.getWork_type());
            if (reqParam.getWorkdivision() != null)
                param.put("workdivision", reqParam.getWorkdivision());
            if (reqParam.getVisit_datetime() != null)
                param.put("visit_datetime", reqParam.getVisit_datetime());
            if (reqParam.getCom_datetime() != null)
                param.put("com_datetime", reqParam.getCom_datetime());
            if (reqParam.getCe_name() != null)
                param.put("ce_name", reqParam.getCe_name());
            if (reqParam.getOwner_name() != null)
                param.put("owner_name", reqParam.getOwner_name());
            if (reqParam.getOwner_sign() != null)
                param.put("owner_sign", reqParam.getOwner_sign());
            if (reqParam.getOper_name() != null)
                param.put("oper_name", reqParam.getOper_name());
            if (reqParam.getOper_sign() != null)
                param.put("oper_sign", reqParam.getOper_sign());
            if (reqParam.getComp_name() != null)
                param.put("comp_name", reqParam.getComp_name());
            if (reqParam.getComp_sign() != null)
                param.put("comp_sign", reqParam.getComp_sign());
            if (reqParam.getTech_name() != null)
                param.put("tech_name", reqParam.getTech_name());
            if (reqParam.getTech_sign() != null)
                param.put("tech_sign", reqParam.getTech_sign());
            if (reqParam.getCreate_date() != null)
                param.put("create_date", reqParam.getCreate_date());

            if (reqParam.getDev_list() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Device List");
                return response;
            }
            param.put("dev_list", reqParam.getDev_list());

            if (reqParam.getSam_info() != null)
                param.put("sam_info", reqParam.getSam_info());

            if (reqParam.getCheck_info() != null)
                param.put("check_info", reqParam.getCheck_info());

            if (reqParam.getDongle_list() != null)
                param.put("dongle_list", reqParam.getDongle_list());

            Integer result = paperlessService.processRegInstallInfo(param);
            if ( result == 1 ) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
            } else {
                response.put("resultCode", ResultCode.HS402_DUPLICATED_EXCEPTION.getCode());
                response.put("resultMessage", ResultCode.HS402_DUPLICATED_EXCEPTION.getDefaultMessage());
            }
        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processRegInstallInfo Error : " + e.getMessage(), e);
        }
        log.debug("************************ processRegInstallInfo Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/install/sign", method = RequestMethod.POST, headers = {"content-type=multipart/*"})
    LinkedHashMap<String, Object> processInstallSign (
            @RequestParam("sign_file") MultipartFile sign_file,
            @RequestParam String req_param
    ) {
        log.debug("************************ processInstallSign Controller end ************************");
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        Gson gson = new Gson();
        ReqParam reqParam = gson.fromJson(req_param, new TypeToken<ReqParam>(){}.getType());
        try {
            HashMap<String, Object> param = new HashMap<>();

            if (reqParam.getSeq() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Seq");
                return response;
            }
            param.put("seq", reqParam.getSeq());

            if (reqParam.getType() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Type");
                return response;
            }
            param.put("type", reqParam.getType());

            String url = paperlessService.uploadSignFile(sign_file, param, "INSTALL");
            if ( url.length() > 0 ) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
                response.put("sign_url", url);
            } else {
                response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
                response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            }
        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processInstallSign Error : " + e.getMessage(), e);
        }
        log.debug("************************ processInstallSign Controller end ************************");

        return response;
    }

    @RequestMapping(value = "/install/info", method = RequestMethod.POST)
    LinkedHashMap<String, Object> processGetInstallInfo(
            @RequestBody HashMap<String, Object> reqMap) {
        log.debug("************************ processGetInstallInfo Controller start ************************");
        Gson gson = new Gson();
        String reqStr = gson.toJson(reqMap.get("req_param"));
        ReqParam reqParam = gson.fromJson(reqStr, new TypeToken<ReqParam>(){}.getType());
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            if (reqParam.getSeq() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Seq");
                return response;
            }
            param.put("seq", reqParam.getSeq());

            LinkedHashMap<String, Object> install_info = paperlessService.processGetInstallInfo(param);
            if ( install_info != null ) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
                response.put("install_info", install_info);
            } else {
                response.put("resultCode", ResultCode.HS404_NOT_FOUND.getCode());
                response.put("resultMessage", ResultCode.HS404_NOT_FOUND.getDefaultMessage());
            }
        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processGetInstallInfo Error : " + e.getMessage(), e);
        }
        log.debug("************************ processGetInstallInfo Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/install/modify", method = RequestMethod.POST)
    LinkedHashMap<String, Object> processModifyInstallInfo(
            @RequestBody HashMap<String, Object> reqMap) {
        log.debug("************************ processModifyInstallInfo Controller start ************************");
        Gson gson = new Gson();
        String reqStr = gson.toJson(reqMap.get("req_param"));
        InstallParam reqParam = gson.fromJson(reqStr, new TypeToken<InstallParam>(){}.getType());
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            if (reqParam.getSeq() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Seq");
                return response;
            }
            param.put("seq", reqParam.getSeq());

            Boolean basic_flag = false;

            if (reqParam.getOrgan_type() != null) {
                param.put("organ_type", reqParam.getOrgan_type());
                basic_flag = true;
            }
            if (reqParam.getOpenday() != null) {
                param.put("openday", reqParam.getOpenday());
                basic_flag = true;
            }
            if (reqParam.getSetupday() != null) {
                param.put("setupday", reqParam.getSetupday());
                basic_flag = true;
            }
            if (reqParam.getWork_type() != null) {
                param.put("work_type", reqParam.getWork_type());
                basic_flag = true;
            }
            if (reqParam.getWorkdivision() != null) {
                param.put("workdivision", reqParam.getWorkdivision());
                basic_flag = true;
            }
            if (reqParam.getVisit_datetime() != null) {
                param.put("visit_datetime", reqParam.getVisit_datetime());
                basic_flag = true;
            }
            if (reqParam.getCom_datetime() != null) {
                param.put("com_datetime", reqParam.getCom_datetime());
                basic_flag = true;
            }
            if (reqParam.getCe_name() != null) {
                param.put("ce_name", reqParam.getCe_name());
                basic_flag = true;
            }
            if (reqParam.getOwner_name() != null) {
                param.put("owner_name", reqParam.getOwner_name());
                basic_flag = true;
            }
            if (reqParam.getOwner_sign() != null) {
                param.put("owner_sign", reqParam.getOwner_sign());
                basic_flag = true;
            }
            if (reqParam.getOper_name() != null) {
                param.put("oper_name", reqParam.getOper_name());
                basic_flag = true;
            }
            if (reqParam.getOper_sign() != null) {
                param.put("oper_sign", reqParam.getOper_sign());
                basic_flag = true;
            }
            if (reqParam.getComp_name() != null) {
                param.put("comp_name", reqParam.getComp_name());
                basic_flag = true;
            }
            if (reqParam.getComp_sign() != null) {
                param.put("comp_sign", reqParam.getComp_sign());
                basic_flag = true;
            }
            if (reqParam.getTech_name() != null) {
                param.put("tech_name", reqParam.getTech_name());
                basic_flag = true;
            }
            if (reqParam.getTech_sign() != null) {
                param.put("tech_sign", reqParam.getTech_sign());
                basic_flag = true;
            }
            if (reqParam.getCreate_date() != null) {
                param.put("create_date", reqParam.getCreate_date());
                basic_flag = true;
            }

            if (reqParam.getDev_list() != null)
                param.put("dev_list", reqParam.getDev_list());
            if (reqParam.getSam_info() != null)
                param.put("sam_info", reqParam.getSam_info());
            if (reqParam.getCheck_info() != null)
                param.put("check_info", reqParam.getCheck_info());
            if (reqParam.getDongle_list() != null)
                param.put("dongle_list", reqParam.getDongle_list());

            Integer result = paperlessService.processModifyInstallInfo(basic_flag, param);
            if ( result == 1 ) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
            } else {
                response.put("resultCode", ResultCode.HS404_NOT_FOUND.getCode());
                response.put("resultMessage", ResultCode.HS404_NOT_FOUND.getDefaultMessage());
            }
        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processModifyInstallInfo Error : " + e.getMessage(), e);
        }
        log.debug("************************ processModifyInstallInfo Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/install/delete", method = RequestMethod.POST)
    LinkedHashMap<String, Object> processDeleteInstallInfo(
            @RequestBody HashMap<String, Object> reqMap) {
        log.debug("************************ processDeleteInstallInfo Controller start ************************");
        Gson gson = new Gson();
        String reqStr = gson.toJson(reqMap.get("req_param"));
        ReqParam reqParam = gson.fromJson(reqStr, new TypeToken<ReqParam>(){}.getType());
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            if (reqParam.getSeq() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Seq");
                return response;
            }
            param.put("seq", reqParam.getSeq());

            Integer result = paperlessService.processDeleteInstallInfo(param);
            if ( result == 1 ) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
            } else {
                response.put("resultCode", ResultCode.HS404_NOT_FOUND.getCode());
                response.put("resultMessage", ResultCode.HS404_NOT_FOUND.getDefaultMessage());
            }
        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processDeleteInstallInfo Error : " + e.getMessage(), e);
        }
        log.debug("************************ processDeleteInstallInfo Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/service/dev", method = RequestMethod.GET)
    LinkedHashMap<String, Object> processGetServiceDeviceList() {
        log.debug("************************ processGetServiceDeviceList Controller start ************************");
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            List<LinkedHashMap<String, Object>> device_list = paperlessService.processGetServiceDeviceList();
            if ( device_list.size() > 0 ) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
                response.put("device_list", device_list);
            } else {
                response.put("resultCode", ResultCode.HS404_NOT_FOUND.getCode());
                response.put("resultMessage", ResultCode.HS404_NOT_FOUND.getDefaultMessage());
            }
        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processGetServiceDeviceList Error : " + e.getMessage(), e);
        }
        log.debug("************************ processGetServiceDeviceList Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/service/check", method = RequestMethod.GET)
    LinkedHashMap<String, Object> processGetServiceCheckList() {
        log.debug("************************ processGetServiceCheckList Controller start ************************");
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            List<LinkedHashMap<String, Object>> check_list = paperlessService.processGetServiceCheckList();
            if ( check_list.size() > 0 ) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
                response.put("check_list", check_list);
            } else {
                response.put("resultCode", ResultCode.HS404_NOT_FOUND.getCode());
                response.put("resultMessage", ResultCode.HS404_NOT_FOUND.getDefaultMessage());
            }
        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processGetServiceCheckList Error : " + e.getMessage(), e);
        }
        log.debug("************************ processGetServiceCheckList Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/service/reg", method = RequestMethod.POST)
    LinkedHashMap<String, Object> processRegServiceInfo(
            @RequestBody HashMap<String, Object> reqMap) {
        log.debug("************************ processRegServiceInfo Controller start ************************");
        Gson gson = new Gson();
        String reqStr = gson.toJson(reqMap.get("req_param"));
        ServiceParam reqParam = gson.fromJson(reqStr, new TypeToken<ServiceParam>() {}.getType());
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            if (reqParam.getSeq() == null) {
                response.put("resultCode", ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Seq");
                return response;
            }
            param.put("seq", reqParam.getSeq());

            if (reqParam.getCode() == null) {
                response.put("resultCode", ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Code");
                return response;
            }
            param.put("code", reqParam.getCode());

            if (reqParam.getOrgan() == null) {
                response.put("resultCode", ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Organ");
                return response;
            }
            param.put("organ", reqParam.getOrgan());

            if (reqParam.getWork_type() == null) {
                response.put("resultCode", ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Work Type");
                return response;
            }
            param.put("work_type", reqParam.getWork_type());

            if (reqParam.getWorkdivision() == null) {
                response.put("resultCode", ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Work Division");
                return response;
            }
            param.put("workdivision", reqParam.getWorkdivision());

            if (reqParam.getReceipt_datetime() == null) {
                response.put("resultCode", ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Receipt DateTime");
                return response;
            }
            param.put("receipt_datetime", reqParam.getReceipt_datetime());

            if (reqParam.getVisit_datetime() != null)
                param.put("visit_datetime", reqParam.getVisit_datetime());
            if (reqParam.getCom_datetime() != null)
                param.put("com_datetime", reqParam.getCom_datetime());
            if (reqParam.getCe_name() != null)
                param.put("ce_name", reqParam.getCe_name());
            if (reqParam.getCe_sign() != null)
                param.put("ce_sign", reqParam.getCe_sign());
            if (reqParam.getOwner_name() != null)
                param.put("owner_name", reqParam.getOwner_name());
            if (reqParam.getOwner_sign() != null)
                param.put("owner_sign", reqParam.getOwner_sign());
            if (reqParam.getOwner_comment() != null)
                param.put("owner_comment", reqParam.getOwner_comment());
            if (reqParam.getCreate_date() != null)
                param.put("create_date", reqParam.getCreate_date());

            if (reqParam.getDev_list() != null)
                param.put("dev_list", reqParam.getDev_list());
            if (reqParam.getFix_list() != null)
                param.put("fix_list", reqParam.getFix_list());
            if (reqParam.getPart_list() != null)
                param.put("part_list", reqParam.getPart_list());

            Integer result = paperlessService.processRegServiceInfo(param);
            if (result == 1) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
            } else {
                response.put("resultCode", ResultCode.HS402_DUPLICATED_EXCEPTION.getCode());
                response.put("resultMessage", ResultCode.HS402_DUPLICATED_EXCEPTION.getDefaultMessage());
            }
        } catch (Exception e) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processRegServiceInfo Error : " + e.getMessage(), e);
        }
        log.debug("************************ processRegServiceInfo Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/service/sign", method = RequestMethod.POST, headers = {"content-type=multipart/*"})
    LinkedHashMap<String, Object> processServiceSign (
            @RequestParam("sign_file") MultipartFile sign_file,
            @RequestParam String req_param
    ) {
        log.debug("************************ processServiceSign Controller end ************************");
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        Gson gson = new Gson();
        ReqParam reqParam = gson.fromJson(req_param, new TypeToken<ReqParam>(){}.getType());
        try {
            HashMap<String, Object> param = new HashMap<>();

            if (reqParam.getSeq() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Seq");
                return response;
            }
            param.put("seq", reqParam.getSeq());

            if (reqParam.getType() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Type");
                return response;
            }
            param.put("type", reqParam.getType());

            String url = paperlessService.uploadSignFile(sign_file, param, "SERVICE");
            if ( url.length() > 0 ) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
                response.put("sign_url", url);
            } else {
                response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
                response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            }
        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processServiceSign Error : " + e.getMessage(), e);
        }
        log.debug("************************ processServiceSign Controller end ************************");

        return response;
    }

    @RequestMapping(value = "/service/modify", method = RequestMethod.POST)
    LinkedHashMap<String, Object> processModifyServiceInfo(
            @RequestBody HashMap<String, Object> reqMap) {
        log.debug("************************ processModifyServiceInfo Controller start ************************");
        Gson gson = new Gson();
        String reqStr = gson.toJson(reqMap.get("req_param"));
        ServiceParam reqParam = gson.fromJson(reqStr, new TypeToken<ServiceParam>() {
        }.getType());
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            if (reqParam.getSeq() == null) {
                response.put("resultCode", ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Seq");
                return response;
            }
            param.put("seq", reqParam.getSeq());

            Boolean basic_flag = false;

            if (reqParam.getVisit_datetime() != null) {
                param.put("visit_datetime", reqParam.getVisit_datetime());
                basic_flag = true;
            }
            if (reqParam.getCom_datetime() != null) {
                param.put("com_datetime", reqParam.getCom_datetime());
                basic_flag = true;
            }
            if (reqParam.getCe_name() != null) {
                param.put("ce_name", reqParam.getCe_name());
                basic_flag = true;
            }
            if (reqParam.getCe_sign() != null) {
                param.put("ce_sign", reqParam.getCe_sign());
                basic_flag = true;
            }
            if (reqParam.getOwner_name() != null) {
                param.put("owner_name", reqParam.getOwner_name());
                basic_flag = true;
            }
            if (reqParam.getOwner_sign() != null) {
                param.put("owner_sign", reqParam.getOwner_sign());
                basic_flag = true;
            }
            if (reqParam.getOwner_comment() != null) {
                param.put("owner_comment", reqParam.getOwner_comment());
                basic_flag = true;
            }
            if (reqParam.getCreate_date() != null) {
                param.put("create_date", reqParam.getCreate_date());
                basic_flag = true;
            }

            if (reqParam.getDev_list() != null)
                param.put("dev_list", reqParam.getDev_list());
            if (reqParam.getFix_list() != null)
                param.put("fix_list", reqParam.getFix_list());
            if (reqParam.getPart_list() != null)
                param.put("part_list", reqParam.getPart_list());

            Integer result = paperlessService.processModifyServiceInfo(basic_flag, param);
            if (result == 1) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
            } else {
                response.put("resultCode", ResultCode.HS404_NOT_FOUND.getCode());
                response.put("resultMessage", ResultCode.HS404_NOT_FOUND.getDefaultMessage());
            }
        } catch (Exception e) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processModifyServiceInfo Error : " + e.getMessage(), e);
        }
        log.debug("************************ processModifyServiceInfo Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/service/delete", method = RequestMethod.POST)
    LinkedHashMap<String, Object> processDeleteServiceInfo(
            @RequestBody HashMap<String, Object> reqMap) {
        log.debug("************************ processDeleteServiceInfo Controller start ************************");
        Gson gson = new Gson();
        String reqStr = gson.toJson(reqMap.get("req_param"));
        ReqParam reqParam = gson.fromJson(reqStr, new TypeToken<ReqParam>(){}.getType());
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            if (reqParam.getSeq() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Seq");
                return response;
            }
            param.put("seq", reqParam.getSeq());

            Integer result = paperlessService.processDeleteServiceInfo(param);
            if ( result == 1 ) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
            } else {
                response.put("resultCode", ResultCode.HS404_NOT_FOUND.getCode());
                response.put("resultMessage", ResultCode.HS404_NOT_FOUND.getDefaultMessage());
            }
        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processDeleteServiceInfo Error : " + e.getMessage(), e);
        }
        log.debug("************************ processDeleteServiceInfo Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/service/info", method = RequestMethod.POST)
    LinkedHashMap<String, Object> processGetServiceInfo(
            @RequestBody HashMap<String, Object> reqMap) {
        log.debug("************************ processGetServiceInfo Controller start ************************");
        Gson gson = new Gson();
        String reqStr = gson.toJson(reqMap.get("req_param"));
        ReqParam reqParam = gson.fromJson(reqStr, new TypeToken<ReqParam>(){}.getType());
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            if (reqParam.getSeq() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Seq");
                return response;
            }
            param.put("seq", reqParam.getSeq());

            LinkedHashMap<String, Object> service_info = paperlessService.processGetServiceInfo(param);
            if ( service_info != null ) {
                response.put("resultCode", ResultCode.HS200_SUCCESS.getCode());
                response.put("service_info", service_info);
            } else {
                response.put("resultCode", ResultCode.HS404_NOT_FOUND.getCode());
                response.put("resultMessage", ResultCode.HS404_NOT_FOUND.getDefaultMessage());
            }
        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processGetServiceInfo Error : " + e.getMessage(), e);
        }
        log.debug("************************ processGetServiceInfo Controller end ************************");
        return response;
    }

    @RequestMapping(value = "/install/excel", method = RequestMethod.POST)
    ModelAndView processDownloadInstallExcelInfo(
            @RequestBody HashMap<String, Object> reqMap) {
        log.debug("************************ processDownloadInstallExcelInfo Controller start ************************");
        ModelAndView modelAndView = new ModelAndView();
        Gson gson = new Gson();
        String reqStr = gson.toJson(reqMap.get("req_param"));
        ReqParam reqParam = gson.fromJson(reqStr, new TypeToken<ReqParam>(){}.getType());
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            if (reqParam.getSeq() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Seq");
                return modelAndView;
            }

            LinkedHashMap<String, Object> service_info = paperlessService.processGetServiceInfo(param);
            if ( service_info != null ) {
                String excelFile = "설치확인서_" + reqParam.getSeq();
                modelAndView.setViewName("excelView");
                modelAndView.addObject("requestType", "INSTALL");
                modelAndView.addObject("format", reqParam.getType());
                modelAndView.addObject("excelData", new ExcelModel(excelFile, Integer.toString(reqParam.getSeq()), service_info));

            } else {
                response.put("resultCode", ResultCode.HS404_NOT_FOUND.getCode());
                response.put("resultMessage", ResultCode.HS404_NOT_FOUND.getDefaultMessage());
                return modelAndView ;
            }

        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processDownloadInstallExcelInfo Error : " + e.getMessage(), e);
        }
        log.debug("************************ processDownloadInstallExcelInfo Controller end ************************");
        return modelAndView;
    }

    @RequestMapping(value = "/service/excel", method = RequestMethod.POST)
    ModelAndView processDownloadServiceExcelInfo(
            @RequestBody HashMap<String, Object> reqMap) {
        log.debug("************************ processDownloadServiceExcelInfo Controller start ************************");
        ModelAndView modelAndView = new ModelAndView();
        Gson gson = new Gson();
        String reqStr = gson.toJson(reqMap.get("req_param"));
        ReqParam reqParam = gson.fromJson(reqStr, new TypeToken<ReqParam>(){}.getType());
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            if (reqParam.getSeq() == null) {
                response.put("resultCode",ResultCode.HS1000_INVALID_PARAMETER.getCode());
                response.put("resultMessage", "Not Exist Seq");
                return modelAndView;
            }

            LinkedHashMap<String, Object> install_info = paperlessService.processGetInstallInfo(param);
            if ( install_info != null ) {
                String excelFile = "설치확인서_" + reqParam.getSeq();
                modelAndView.setViewName("excelView");
                modelAndView.addObject("requestType", "SERVICE");
                modelAndView.addObject("format", reqParam.getType());
                modelAndView.addObject("excelData", new ExcelModel(excelFile, Integer.toString(reqParam.getSeq()), install_info));

            } else {
                response.put("resultCode", ResultCode.HS404_NOT_FOUND.getCode());
                response.put("resultMessage", ResultCode.HS404_NOT_FOUND.getDefaultMessage());
                return modelAndView ;
            }

        } catch ( Exception e ) {
            response.put("resultCode", ResultCode.HS500_EXCEPTION.getCode());
            response.put("resultMessage", ResultCode.HS500_EXCEPTION.getDefaultMessage());
            log.error("PaperlessController processDownloadServiceExcelInfo Error : " + e.getMessage(), e);
        }
        log.debug("************************ processDownloadServiceExcelInfo Controller end ************************");
        return modelAndView;
    }
}
