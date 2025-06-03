package com.hoanganhdangcode.yahooooo.ViewModel;

import static java.security.AccessController.getContext;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hoanganhdangcode.yahooooo.Model.UserData;
import com.hoanganhdangcode.yahooooo.Repository.WallRepository;
import com.hoanganhdangcode.yahooooo.Util.Utils;

public class WallViewModel extends ViewModel {
    private String targetuid, currentuid;
    private final WallRepository repository = new WallRepository();
    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> des = new MutableLiveData<>();
    private final MutableLiveData<String> avatar = new MutableLiveData<>();
    private final MutableLiveData<String> background = new MutableLiveData<>();
    private final MutableLiveData<Integer> status = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<Integer> getStatus() {
        return status;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getDes() {
        return des;
    }

    public LiveData<String> getAvatar() {
        return avatar;
    }

    public LiveData<String> getBackground() {
        return background;
    }
    public void setTargetuid(String uid) {
        this.targetuid = uid;
    }
    public String getTargetuidUid() {
        return targetuid;
    }
public void  setStatus(int value){
        status.setValue(value);
    }
    public void setIsLoading(Boolean value) {
        isLoading.setValue(value);
    }
    public void setName(String value) {
        name.setValue(value);
    }

    public void setDes(String value) {
        des.setValue(value);
    }

    public void setAvatar(String value) {
        avatar.setValue(value);
    }

    public void setBackground(String value) {
        background.setValue(value);
    }
    public void loadData(String targetuid, String currentUid) {
        targetuid= this.targetuid;
        currentuid = currentUid;
        setIsLoading(true);
        repository.load(targetuid,currentUid,new WallRepository.LoadCallback() {
            @Override
            public void onSuccess(UserData userData) {
                if (userData != null) {
                    setName(userData.getName());
                    setDes(userData.getDescription());
                    setAvatar(userData.getAvatar());
                    setBackground(userData.getBackground());
                    setIsLoading(false);

                } else {
                    // Handle the case where userData is null
                    setIsLoading(false);
                    setName("Unknown User");
                    setDes("No description available");
                    setAvatar("default_avatar_url"); // Replace with a default avatar URL
                    setBackground("default_background_url"); // Replace with a default background URL
                    setStatus(-1);
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle the error case
                setIsLoading(false);
                setName("Error loading user");
                setDes("Failed to load description");
                setAvatar("default_avatar_url"); // Replace with a default avatar URL
                setBackground("default_background_url"); // Replace with a default background URL
            }

            @Override
            public void status(int status) {
                setStatus(status);
            }


        });
    }


}
