package com.hoanganhdangcode.yahooooo.ViewModel;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hoanganhdangcode.yahooooo.Repository.AuthRepository;

public class AuthViewModel extends ViewModel {
    private final AuthRepository repository = new AuthRepository();

    private final MutableLiveData<Boolean> signupSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> signupMessage = new MutableLiveData<>();

    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> loginMessage = new MutableLiveData<>();
    private final MutableLiveData<String> loginUid = new MutableLiveData<>();
    public LiveData<String> getLoginUid() { return loginUid; }


    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LiveData<Boolean> getSignupSuccess() { return signupSuccess; }
    public LiveData<String> getSignupMessage() { return signupMessage; }

    public LiveData<Boolean> getLoginSuccess() { return loginSuccess; }
    public LiveData<String> getLoginMessage() { return loginMessage; }

    public LiveData<Boolean> getIsLoading() { return isLoading; }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void signup(String name, int gender, String birth, String phone, String password) {
        isLoading.setValue(true); // Bắt đầu loading
        repository.signup(name, gender, birth, phone, password, new AuthRepository.SignupCallback() {
            @Override
            public void onSuccess(String uid) {
                loginUid.postValue(uid);
                isLoading.postValue(false);
                signupSuccess.postValue(true);
                signupMessage.postValue("Đăng ký thành công");
            }

            @Override
            public void onFailure(Exception e) {
                isLoading.postValue(false);
                signupSuccess.postValue(false);
                signupMessage.postValue(e.getMessage());
            }
        });
    }

    public void login(String phone, String password) {
        isLoading.setValue(true); // Bắt đầu loading
        repository.signin( phone,  password, new AuthRepository.SigninCallback(){
            @Override
            public void onSuccess(String uid) {
            loginUid.postValue(uid);
            isLoading.postValue(false);
            loginSuccess.postValue(true);
            loginMessage.postValue("Đăng nhập thành công");
            }

            @Override
            public void onFailure(Exception e) {

                isLoading.postValue(false);
                loginSuccess.postValue(false);
                loginMessage.postValue(e.getMessage());
                loginUid.postValue(null);

            }

            });
        }


}
