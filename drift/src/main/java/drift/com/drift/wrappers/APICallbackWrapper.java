package drift.com.drift.wrappers;

/**
 * Created by eoin on 28/07/2017.
 */


public interface APICallbackWrapper<T> {

    void onResponse(T response);

}