package kr.co.opensysnet.paperless.server;

import kr.co.opensysnet.paperless.model.request.ReqBase;
import kr.co.opensysnet.paperless.model.request.ReqInstall;
import kr.co.opensysnet.paperless.model.request.ReqLogin;
import kr.co.opensysnet.paperless.model.request.ReqSchedule;
import kr.co.opensysnet.paperless.model.request.ReqService;
import kr.co.opensysnet.paperless.model.request.ReqStore;
import kr.co.opensysnet.paperless.model.request.ReqWorking;
import kr.co.opensysnet.paperless.model.response.RespBase;
import kr.co.opensysnet.paperless.model.response.RespCheck;
import kr.co.opensysnet.paperless.model.response.RespCheckInfo;
import kr.co.opensysnet.paperless.model.response.RespCheckInfoCheck;
import kr.co.opensysnet.paperless.model.response.RespDevice;
import kr.co.opensysnet.paperless.model.response.RespDeviceInfo;
import kr.co.opensysnet.paperless.model.response.RespDeviceModelInfo;
import kr.co.opensysnet.paperless.model.response.RespInstall;
import kr.co.opensysnet.paperless.model.response.RespInstallInfo;
import kr.co.opensysnet.paperless.model.response.RespSchedule;
import kr.co.opensysnet.paperless.model.response.RespScheduleInfo;
import kr.co.opensysnet.paperless.model.response.RespService;
import kr.co.opensysnet.paperless.model.response.RespServiceInfo;
import kr.co.opensysnet.paperless.model.response.RespSign;
import kr.co.opensysnet.paperless.model.response.RespStore;
import kr.co.opensysnet.paperless.model.response.RespStoreInfo;
import kr.co.opensysnet.paperless.model.response.RespWorking;
import kr.co.opensysnet.paperless.model.response.RespWorkingInfo;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitAPI {
    // 로그인
    @POST("login")
    Call<RespBase> reqLogin(@Body ReqBase<ReqLogin> body);
    // 로그아웃
    @POST("logout")
    Call<RespBase> reqLogout(@Body ReqBase<ReqLogin> body);
    // 상점지도
    @POST("store/list")
    Call<RespStore<RespStoreInfo>> reqStoreList(@Body ReqBase<ReqStore> body);
    // 작업 일정
    @POST("schedule/list")
    Call<RespSchedule<RespScheduleInfo>> reqScheduleList(@Body ReqBase<ReqSchedule> body);
    // 작업 목록
    @POST("working/list")
    Call<RespWorking<RespWorkingInfo>> reqWorkingList(@Body ReqBase<ReqWorking> body);
    // 설치확인서 기기,모델 목록
    @GET("install/dev")
    Call<RespDevice<RespDeviceInfo<RespDeviceModelInfo>>> reqInstallDev();
    // 설치확인서 등록
    @POST("install/reg")
    Call<RespBase> reqInstallReg(@Body ReqBase<ReqInstall> body);
    // 설치확인서 변경
    @POST("install/modify")
    Call<RespBase> reqInstallModify(@Body ReqBase<ReqInstall> body);
    // 설치확인서 사인 SignBodyUtil.getReqParamFromData(int, String) 사용해서 param 획득
    @Multipart
    @POST("install/sign")
    Call<RespSign> reqInstallSign(@Part MultipartBody.Part sign_file, @Part("req_param") String param);
    // 설치확인서 조회
    @POST("install/info")
    Call<RespInstall<RespInstallInfo>> reqInstallInfo(@Body ReqBase<ReqInstall> body);
    // 설치확인서 삭제
    @POST("install/delete")
    Call<RespBase> reqInstallDelete(@Body ReqBase<ReqInstall> body);
    // 서비스확인서 기기,모델 목록
    @GET("service/dev")
    Call<RespDevice<RespDeviceInfo<RespDeviceModelInfo>>> reqServiceDev();
    // 서비스확인서 점검목록 요청
    @GET("service/check")
    Call<RespCheck<RespCheckInfo<RespCheckInfoCheck>>> reqServiceCheck();
    // 서비스확인서 등록
    @POST("service/reg")
    Call<RespBase> reqServiceReg(@Body ReqBase<ReqService> body);
    // 서비스확인서 수정
    @POST("service/modify")
    Call<RespBase> reqServiceModify(@Body ReqBase<ReqService> body);
    // 서비스확인서 사인 SignBodyUtil.getReqParamFromData(int, String) 사용해서 param 획득
    @Multipart
    @POST("service/sign")
    Call<RespSign> reqServiceSign(@Part MultipartBody.Part sign_file, @Part("req_param") String param);
    // 서비스확인서 조회
    @POST("service/info")
    Call<RespService<RespServiceInfo>> reqServiceInfo(@Body ReqBase<ReqService> body); // todo
    // 서비스확인서 삭제
    @POST("service/delete")
    Call<RespBase> reqServiceDelete(@Body ReqBase<ReqService> body);
}
