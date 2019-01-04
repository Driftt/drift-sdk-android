package drift.com.drift.wrappers

/**
 * Created by eoin on 28/07/2017.
 */


interface APICallbackWrapper<T> {

    fun onResponse(response: T)

}