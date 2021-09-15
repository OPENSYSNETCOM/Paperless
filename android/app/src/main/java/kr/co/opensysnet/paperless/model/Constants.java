package kr.co.opensysnet.paperless.model;

public class Constants {
    public static class IntentExtra {
        public static final String EXTRA_KEY_FRAGMENT_STATE = "extra_key_fragment_state";
        public static final String EXTRA_KEY_BUNDLE = "extra_key_bundle";
        public static final String EXTRA_KEY_URL = "extra_key_url";
        public static final String EXTRA_KEY_ADD_BACK_STACK = "extra_key_add_back_stack";
        public static final String EXTRA_KEY_ADD_FRAGMENT = "extra_key_add_fragment";
        public static final String EXTRA_KEY_ADD_BACK_STACK_NAME = "extra_key_add_back_stack_name";
        public static final String EXTRA_KEY_TARGET_FRAGMENT = "extra_key_target_fragment";
    }

    public static class Preference {
        public static class MainKey {
            public static final String LOGIN_INFO = "pref_main_key_login_info";
        }

        public static class SubKey {
            public static final String USER_ID = "pref_sub_key_user_id";
            public static final String USER_NAME = "pref_sub_key_user_name";
            public static final String PASSWORD = "pref_sub_key_password";
        }
    }

    public static class FragmentState {
        public static final String INTRO = "fragment_state_intro";
        public static final String LOGIN = "fragment_state_login";
        public static final String STORE_MAP = "fragment_state_store_map";
        public static final String WORK_LIST = "fragment_state_work_list";
        public static final String WORK_SCHEDULE = "fragment_state_work_schedule";
        public static final String WORK_PAPER_REGISTER = "fragment_state_work_paper_register";
        public static final String WORK_PAPER_SIGN = "fragment_state_work_paper_sign";
        public static final String SERVICE_PAPER_REGISTER = "fragment_state_service_paper_register";
        public static final String SERVICE_PAPER_SIGN = "fragment_state_service_paper_sign";

    }

    public static class ServerResultCode {
        public static final int SUCCESS = 200;
        public static final int NOT_FOUND = 404;

    }

    public static class NotificationType{

    }

    public static class BundleKey {
        public static final String IS_MODIFY = "bundle_key_is_modify";
        public static final String PAPER_SEQ = "bundle_key_paper_seq";
        public static final String PAPER_CODE = "bundle_key_paper_code";
        public static final String PAPER_ORGAN = "bundle_key_paper_organ";
        public static final String PAPER_WORK_DIVISION = "bundle_key_paper_work_division";
        public static final String PAPER_CE_NAME = "bundle_key_paper_ce_name";
    }
}
