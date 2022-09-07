package in.co.fastride.apihandler;


public class AppConstant {
    public static final String baseURL = "https://fastride.co.in";
    public static final String userLoginURL =baseURL + "/api/Account/Login";
    public static final String userRegistrationURL =baseURL +"/api/Account/RegisterUser";
    public static final String userOTPConfirmURL = baseURL+"/api/Account/RegistrationOTPConfirm";
    public static final String resendOtpRequestURL =baseURL +"/api/Account/ResendOTPRequest";
    public static final String resetPasswordRequestURL = baseURL+"/api/Account/ResetPasswordRequest";
    public static final String userChangePasswordOtpURL = baseURL + "/Api/Account/ResetPasswordOTPConfirm";
    public static final String resetPasswordURL =baseURL +"/api/Account/ResetPassword";
    public static final String userLogoutURL =baseURL +"/api/Account/Logout";
    public static final String UpdateProfileImageURL =baseURL +"/api/Account/UpdateProfileImage";
    public static final String UpdateProfileURL =baseURL +"/api/Account/UpdateProfile";
    public static final String GetDriverDetailsURL =baseURL +"/api/Driver/GetDriverDetails";
    public static final String AllCityListURL =baseURL +"/api/Settings/AllCityList";
    public static final String VehicleTypesURL =baseURL +"/api/Settings/VehicleTypes";
    public static final String SaveDriverVehicleDetailsURL =baseURL +"/api/Driver/SaveDriverVehicleDetails";
    public static final String GetBloodGroupsURL =baseURL +"/api/Settings/BloodGroups";


}
