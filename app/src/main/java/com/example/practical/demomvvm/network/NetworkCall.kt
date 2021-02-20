

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.multidex.BuildConfig
import com.example.demomvvm.Utils.Utils
import com.example.practical.R
import com.example.practical.demomvvm.network.ApiInterface
import com.example.practical.demomvvm.network.listeners.DefaultActionPerformer
import com.example.practical.demomvvm.network.listeners.NoInternetListner
import com.example.practical.demomvvm.network.listeners.RetrofitRawResponseListener
import com.example.practical.demomvvm.network.listeners.RetrofitResponseListener
import com.google.android.material.snackbar.Snackbar



import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class NetworkCall constructor(context: Context) : Callback<ResponseBody> {

    private var customBaseURL: String? = null

    private var REQUEST_TYPE = REQUEST_TYPE_POST

    private var endPoint = ""
    private var mContext: Context? = null

    private var shouldPromptOnNoInternet = true
    private var noInternetPromptType = NO_INTERNET_PROMPT_TOAST
    private var snackbarView: View? = null
    private var noInternetListner: NoInternetListner? = null

    private var retrofitResponseListener: RetrofitResponseListener? = null

    private var retrofitRawResponseListener: RetrofitRawResponseListener? = null

    private var requestObject: Any? = null
    private var requestParams: HashMap<String, String>? = HashMap()
    private var requestFiles: HashMap<String, File>? = null
    private var headers = HashMap<String, String>()

    private var call: Call<ResponseBody>? = null

    private val printBuilder = StringBuilder("\n API EndPoint : ")


    private var isMultipartCall = false

    /* Internet Handeling*/
    private val isConnectedToInternet: Boolean
        get() {
            val connectivity =
                mContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivity.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isAvailable && activeNetwork.isConnected
        }
    private//.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    val instance: ApiInterface
        get() {

            if (customBaseURL != null) {


                val interceptor = HttpLoggingInterceptor()
                if (BuildConfig.DEBUG) {
                    interceptor.level = HttpLoggingInterceptor.Level.BODY
                } else {
                    interceptor.level = HttpLoggingInterceptor.Level.NONE
                }

                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(interceptor).build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(customBaseURL!!)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                return retrofit.create(ApiInterface::class.java)

            } else {
                if (apiInterface == null) {
                    val interceptor = HttpLoggingInterceptor()
                    if (BuildConfig.DEBUG) {
                        interceptor.level = HttpLoggingInterceptor.Level.BODY
                    } else {
                        interceptor.level = HttpLoggingInterceptor.Level.NONE
                    }

                    val client = OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(interceptor).build()

                    val retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    apiInterface = retrofit.create(ApiInterface::class.java)

                }

                return apiInterface as ApiInterface
            }
        }

    internal val defaultMessageError: ArrayList<String>
        get() {
            val message = ArrayList<String>()
            message.add(mContext!!.getString(R.string.str_something_went_wrong))
            return message
        }

    init {
        this.mContext = context
    }


    fun setEndPoint(endPoint: String): NetworkCall {
        this.endPoint = endPoint
        return this
    }

    fun setNoInternetPromptType(noInternetPromptType: Int): NetworkCall {
        this.noInternetPromptType = noInternetPromptType
        return this
    }

    fun setNoInternetPromptType(noInternetPromptType: Int, snackBarView: View): NetworkCall {
        this.noInternetPromptType = noInternetPromptType
        this.snackbarView = snackBarView
        return this
    }

    fun shouldPromptOnNoInternet(shouldPromptOnNoInternet: Boolean): NetworkCall {
        this.shouldPromptOnNoInternet = shouldPromptOnNoInternet
        return this
    }

    fun setNoInternetListner(noInternetListner: NoInternetListner): NetworkCall {
        this.noInternetListner = noInternetListner
        return this
    }

    fun setResponseListener(retrofitRxResponseListener: RetrofitResponseListener): NetworkCall {
        this.retrofitResponseListener = retrofitRxResponseListener
        return this
    }


    fun setRetrofitRawResponseListener(retrofitRawResponseListener: RetrofitRawResponseListener): NetworkCall {
        this.retrofitRawResponseListener = retrofitRawResponseListener
        return this
    }


    fun setRequestObject(requestObject: Any): NetworkCall {
        this.requestObject = requestObject
        return this
    }

    fun setRequestParams(params: HashMap<String, String>): NetworkCall {
        this.requestParams = params
        return this
    }

    fun setHeaders(headers: HashMap<String, String>): NetworkCall {
        this.headers = headers
        return this
    }

    fun setFiles(fileParams: HashMap<String, File>): NetworkCall {
        this.requestFiles = fileParams
        this.isMultipartCall = true
        return this
    }

    fun setMultipartCall(multipartCall: Boolean) {
        isMultipartCall = multipartCall
    }

    private fun showNoInternetAlert() {

        val builder = AlertDialog.Builder(mContext!!)
        builder.setTitle(mContext!!.resources.getString(R.string.app_name))
        builder.setCancelable(true)

        builder.setMessage(mContext!!.getString(R.string.str_no_internet))
        builder.setPositiveButton(mContext!!.getString(android.R.string.ok)) { dialog, which -> dialog.cancel() }
        builder.create().show()
    }
    fun makeEmptyRequestCall() {
        requestParams = HashMap()
        makeCall()
    }

    fun makeCall(): NetworkCall {

        if (requestObject == null && requestParams == null) {
            Log.e("Error", "No Request Source is Provided")
        } else {
            if (isConnectedToInternet) {

                Log.e("Error", "API EndPoint => $endPoint")
                printBuilder.append(endPoint).append("\n\n").append("Headers\n")

                if (actionPerformer != null) {
                    actionPerformer!!.onActionPerform(headers, requestParams!!)
                }

                if (headers.size > 0) {
                    for ((key, value) in headers) {
                        Log.e("Error", "$key=>$value")
                        printBuilder.append(key).append("=>").append(value).append("\n")
                    }
                } else {
                    Log.e("Error", "headers are empty")
                    printBuilder.append("Headers are Empty")
                }


                if (retrofitResponseListener != null) {
                    retrofitResponseListener!!.onPreExecute()
                }


                if (retrofitRawResponseListener != null) {
                    retrofitRawResponseListener!!.onPreExecute()
                }


                if (requestObject != null) {
                    makeRequestWithObject(requestObject!!)
                } else {
                    makeRequestWithParams(requestParams!!)
                }


            } else {
                if (shouldPromptOnNoInternet) {
                    when (noInternetPromptType) {
                        NO_INTERNET_PROMPT_TOAST -> Toast.makeText(
                            mContext,
                            mContext!!.getText(R.string.str_no_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                        NO_INTERNET_PROMPT_SNACKBAR -> if (snackbarView != null) {
                            Snackbar.make(
                                snackbarView!!,
                                mContext!!.getText(R.string.str_no_internet),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        NO_INTERNET_PROMPT_ALERT -> showNoInternetAlert()
                    }
                }
                if (noInternetListner != null) {
                    noInternetListner!!.onNoInternet()
                }
            }
        }

        return this
    }

    fun cancelRequest() {
        call!!.cancel()
    }

    private fun makeRequestWithObject(requestClass: Any) {

        printBuilder.append("\n\n").append("Request Object\n")
        printBuilder.append(requestClass.toString()).append("\n\n")
        Log.e("Error", requestClass.toString())

        call = instance.APICall(endPoint, headers, requestClass)
        call!!.enqueue(this)
    }


    private fun makeRequestWithParams(requestParams: HashMap<String, String>) {

        printBuilder.append("\n\n").append("Request Params\n")

        if (isMultipartCall) {
            val bodyParams = HashMap<String, RequestBody>()

            if (requestParams.size > 0) {
                for ((key, value) in requestParams) {
                    Log.e("Error", "$key=>$value")
                    printBuilder.append(key).append("=>").append(value).append("\n")
                    bodyParams[key] = createPartFromString(value)
                }
            } else {
                Log.e("Error", "Param are empty")
                printBuilder.append(" Params are empty ")
            }


            if (requestFiles != null && requestFiles!!.size > 0) {
                printBuilder.append("\n\n").append("Files to Upload\n")
                for ((key, value) in requestFiles!!) {
                    Log.e("Error", key + "=>" + value.path)
                    printBuilder.append(key).append("=>").append(value.path).append("\n")
                    val fileName = key + "\"; filename=\"" + value.name
                    bodyParams[fileName] = createPartFromFile(value)
                }
            }

            call = instance.APIMultipartCall(endPoint, headers, bodyParams)
            call!!.enqueue(this)
        } else {

            if (requestParams.size > 0) {
                for ((key, value) in requestParams) {
                    Log.e("Error", "$key=>$value")
                    requestParams[key] = value.trim { it <= ' ' }
                    printBuilder.append(key).append("=>").append(value).append("\n")
                }
            } else {
                Log.e("Error", "Param are empty")
                printBuilder.append(" Params are empty ")
            }
            when (REQUEST_TYPE) {
                REQUEST_TYPE_GET -> call = instance.APICall(endPoint, headers)
                else -> call = instance.APICall(endPoint, headers, requestParams)
            }

            call!!.enqueue(this)
        }

    }
    private fun createPartFromFile(file: File): RequestBody {
        return RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file)
    }
    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        handleResponse(response)
    }
    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        handleError(t)

        printBuilder.append("\n\nResponse\n")
        printBuilder.append("Call to the API Failed")
        printBuilder.append("\n\nThank you\n\n")
        copyToClipBoard()
    }
    private fun handleResponse(response: Response<ResponseBody>) {
        if (response.code() == 401) {
            return
        }
        printBuilder.append("\n\nStatusCode : ").append(response.code().toString()).append("")
        try {
            if (response.body() != null) {

                val body = response.body()!!.string()
                Log.e("Error", "Success Response : $body")

                printBuilder.append("\n\nResponse\n")
                printBuilder.append(body)
                printBuilder.append("\n\n Thank you\n\n")

                copyToClipBoard()

                val jsonObject = JSONObject(body)

                if (retrofitRawResponseListener != null) {
                    retrofitRawResponseListener!!.onSuccess(response.code(), jsonObject, body)
                }

                if (jsonObject.optBoolean("status") == true) {
                    if (retrofitResponseListener != null) {
                        retrofitResponseListener!!.onSuccess(
                            response.code(),
                            jsonObject.optJSONObject("data"),
                            body
                        )
                    }
                } else {
                    val errorMessage = Utils.getMessageFromResponseObject(mContext!!, jsonObject)
                    if (retrofitResponseListener != null) {
                        retrofitResponseListener!!.onError(response.code(), errorMessage)
                    }
                }
            } else {
                val body = response.errorBody()!!.string()
                Log.e("Error", "Error Body =>$body")

                printBuilder.append("\n\nResponse\n")
                printBuilder.append(body)
                printBuilder.append("\n\nThank you\n\n")
                copyToClipBoard()

                var message = defaultMessageError

                // This code just to handle error response which is not there in our application still it is there for future purpose
                try {
                    val `object` = JSONObject(body)
                    message = Utils.getMessageFromResponseObject(mContext!!, `object`)
                } catch (e: JSONException) {
                    e.printStackTrace()
                } finally {

                    if (retrofitRawResponseListener != null) {
                        retrofitRawResponseListener!!.onError(response.code(), message)
                    }

                    if (retrofitResponseListener != null) {
                        retrofitResponseListener!!.onError(response.code(), message)
                        if (response.code() == 401) {
                            Toast.makeText(mContext, R.string.str_session_expried_msg, Toast.LENGTH_SHORT).show()

                            /* val sessionManager = SessionManager(mContext)
                             sessionManager.clearSession()
                             sessionManager.setFlagFromKey(PreferenceKeys.KEY_IS_FIRST_TIME, true)
                             sessionManager.setFlagFromKey( PreferenceKeys.KEY_IS_FIRST_TIME_INVITE,true )
                            val intent = Intent(mContext, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            mContext!!.startActivity(intent)
                            (mContext as AppCompatActivity).finish()*/


                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            if (retrofitResponseListener != null) {
                retrofitResponseListener!!.onError(response.code(), defaultMessageError)
            }

            if (retrofitRawResponseListener != null) {
                retrofitRawResponseListener!!.onError(response.code(), defaultMessageError)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            if (retrofitResponseListener != null) {
                retrofitResponseListener!!.onError(response.code(), defaultMessageError)
            }
            if (retrofitRawResponseListener != null) {
                retrofitRawResponseListener!!.onError(response.code(), defaultMessageError)
            }
        }

    }
    private fun handleError(e: Throwable) {
        e.printStackTrace()
        if (retrofitResponseListener != null) {
            retrofitResponseListener!!.onError(500, defaultMessageError)
        }
        if (retrofitRawResponseListener != null) {
            retrofitRawResponseListener!!.onError(500, defaultMessageError)
        }
    }

    private fun copyToClipBoard() {
        if (isDubuggable) {
            val clipboard =
                mContext!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("API Data", printBuilder.toString())
            assert(clipboard != null)
            clipboard.setPrimaryClip(clip)
        }
    }
    fun setCustomBaseURL(customBaseURL: String): NetworkCall {
        this.customBaseURL = customBaseURL
        return this
    }
    private fun createPartFromString(value: String): RequestBody {
        return RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), value)
    }

    fun setRequestType(requestType: Int): NetworkCall {
        REQUEST_TYPE = requestType
        return this
    }


    companion object {

        private const val MULTIPART_FORM_DATA = "multipart/form-data"
        const val NO_INTERNET_PROMPT_TOAST = 0
        const val NO_INTERNET_PROMPT_SNACKBAR = 1
        const val NO_INTERNET_PROMPT_ALERT = 2
        const val REQUEST_TYPE_GET = 1
        const val REQUEST_TYPE_POST = 0

        private var BASE_URL = "http://sd2-hiring.herokuapp.com/" // this will be affected by flavor in gradle

        private var actionPerformer: DefaultActionPerformer? = null
        private var isDubuggable = false


        fun with(context: Context): NetworkCall {
            return NetworkCall(context)
        }

        fun setActionPerformer(actionPerformer: DefaultActionPerformer) {
            NetworkCall.actionPerformer = actionPerformer
        }

        fun setIsDubuggable(isDubuggable: Boolean) {
            NetworkCall.isDubuggable = isDubuggable
        }

        // Retrofit API Interface Instance Handling
        private var apiInterface: ApiInterface? = null

        fun setBASE_URL(BASE_URL: String) {
            NetworkCall.BASE_URL = BASE_URL
        }
    }
}



