package com.loopme.webservice;

import com.loopme.Constants;
import com.loopme.ResourceInfo;
import com.loopme.ad.AdParams;
import com.loopme.common.LoopMeError;
import com.loopme.models.response.ResponseJsonModel;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;


public class HttpService {

    private static final String LOG_TAG = HttpService.class.getSimpleName();
    private static final String REQUEST_BODY_MEDIA_TYPE = "application/json; charset=utf-8";
    private Call<ResponseJsonModel> mFetchAdCall;
    private Call<String> mDownloadCall;

    public void cancel() {
        if (mFetchAdCall != null) {
            mFetchAdCall.cancel();
        }
        if (mDownloadCall != null) {
            mDownloadCall.cancel();
        }
    }

    public Response<ResponseJsonModel> fetchAdSync(JSONObject data) throws IOException {
        Retrofit retrofit = RetrofitFabric.getRetrofit(Constants.RetrofitType.FETCH, null);
        ApiService service = retrofit.create(ApiService.class);
        RequestBody requestBody = retrieveRequestBody(data);
        mFetchAdCall = service.fetchAd(requestBody);
        return mFetchAdCall.execute();
    }


    public Response<ResponseJsonModel> fetchAdSync(String url) throws IOException {
        Retrofit retrofit = RetrofitFabric.getRetrofit(Constants.RetrofitType.FETCH_BY_URL, url);
        ApiService service = retrofit.create(ApiService.class);
        mFetchAdCall = service.fetchAd();
        return mFetchAdCall.execute();
    }

    public Response<String> downLoadSync(ResourceInfo resourceInfo) throws IOException {
        Retrofit retrofit = RetrofitFabric.getRetrofit(Constants.RetrofitType.DOWNLOAD, resourceInfo.getUrl());
        ApiService apiService = retrofit.create(ApiService.class);
        mDownloadCall = apiService.downloadFile(resourceInfo.getResourceName());
        return mDownloadCall.execute();
    }

    private RequestBody retrieveRequestBody(JSONObject data) {
        return RequestBody.create(okhttp3.MediaType.parse(REQUEST_BODY_MEDIA_TYPE), data.toString());
    }

    public interface HttpServiceCallback {
        void onSuccessResult(AdParams adParams);

        void onErrorResult(LoopMeError loopMeError);
    }
}