package maima.chakulapap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterFood extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<DataFood> data= Collections.emptyList();
    DataFood current;
    int currentPos=0;
    String msg;
    ProgressDialog progressDialog;
    UserLocalStore userLocalStore;

    // create constructor to innitilize context and data sent from MainActivity
    public AdapterFood(Context context, List<DataFood> data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data=data;
        userLocalStore = new UserLocalStore(context);
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.container_food, parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final String loggedUser = userLocalStore.getLoggedInUser().name; //The current logged in user

        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder= (MyHolder) holder;
        final DataFood current=data.get(position);
        myHolder.textFishName.setText(current.fishName);
        myHolder.textSize.setText("Size: " + current.sizeName);
        myHolder.textType.setText("Category: " + current.catName);
        myHolder.textPrice.setText("Ksh. " + current.price);
        myHolder.textProvider.setText("Provider Name: " + current.providerName);
        myHolder.textPrice.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        myHolder.order_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(final View view){
                //Order Button click event
                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(view.getContext())
                        //set message, title, and icon
                        .setTitle("Confirm order")
                        .setMessage("Are you sure you want to order "+ current.fishName + "?")
                        //.setIcon(R.drawable.icon) will replace icon with name of existing icon from project
                        //set three option buttons
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                progressDialog = new ProgressDialog(context);
                                progressDialog.setCancelable(false);
                                progressDialog.setTitle("Processing...");
                                progressDialog.setMessage("Please wait...");
                                progressDialog.show();

                                //Send the order details to the orders table in db
                                insertToDatabase(current.fishImage, current.fishName, current.catName, current.sizeName, Integer.toString(current.price), current.providerName, loggedUser );



                            }
                        })//setPositiveButton


                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                Snackbar.make(view, "Your order has been cancelled", Snackbar.LENGTH_LONG)
                                        .setAction("OK", new View.OnClickListener(){
                                            @Override
                                            public void onClick(View v) {
                                                //Toast.makeText(view.getContext(), "...", Toast.LENGTH_SHORT).show();

                                                //Process order Logic goes below here...

                                            }
                                        }).show();
                            }
                        })//setNegativeButton

                        .create();
                myQuittingDialogBox.show();

            }
        });

        // load image into imageview using glide
        Glide.with(context).load("http://dev.oscorpltd.com/uploads/" + current.fishImage)
                .placeholder(R.drawable.ic_img_error)
                .error(R.drawable.ic_img_error)
                .into(myHolder.ivFish);

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{

        TextView textFishName;
        ImageView ivFish;
        TextView textSize;
        TextView textType;
        TextView textPrice;
        TextView textProvider;
        Button order_button;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textFishName= (TextView) itemView.findViewById(R.id.textFishName);
            ivFish= (ImageView) itemView.findViewById(R.id.ivFish);
            textSize = (TextView) itemView.findViewById(R.id.textSize);
            textType = (TextView) itemView.findViewById(R.id.textType);
            textPrice = (TextView) itemView.findViewById(R.id.textPrice);
            textProvider = (TextView) itemView.findViewById(R.id.textProvider);
            order_button = (Button) itemView.findViewById(R.id.order_button);
        }

    }

    /**
     * Here we take in 6 string parameters that we will push to our orders database
     */
    protected void insertToDatabase(final String order_img, String order_name, String order_cat, String order_size, String order_price, String provider_name, String customer_name){
        class  SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            public String doInBackground(String... params) { //Will be executed when we execute AsyncTask
                String paramOrder_img = params[0];
                String paramOrder_name = params[1];
                String paramOrder_cat = params[2];
                String paramOrder_size = params[3];
                String paramOrder_price = params[4];
                String paramProvider_name = params[5];
                String paramCustomer_name = params[6];

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("order_img", paramOrder_img));
                nameValuePairs.add(new BasicNameValuePair("order_name", paramOrder_name));
                nameValuePairs.add(new BasicNameValuePair("order_cat", paramOrder_cat));
                nameValuePairs.add(new BasicNameValuePair("order_size", paramOrder_size));
                nameValuePairs.add(new BasicNameValuePair("order_price", paramOrder_price));
                nameValuePairs.add(new BasicNameValuePair("provider_name", paramProvider_name));
                nameValuePairs.add(new BasicNameValuePair("customer_name", paramCustomer_name));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://dev.oscorpltd.com/InsertOrders.php"
                    );

                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpClient.execute(httpPost);
                    HttpEntity entity = response.getEntity();
                }
                catch (ClientProtocolException e){

                }

                catch (IOException e){

                }
                return paramOrder_name;
            }

            protected void onPostExecute(String result){ //Will execute after the execution of doInBackground
                super.onPostExecute(result);
                //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                progressDialog.hide();
                AlertDialog myInsertedDialogBox = new AlertDialog.Builder(context)
                        //set message, title, and icon
                        .setTitle("Order sent")
                        .setMessage("Your order of " + result +  " has been sent")
                        //.setIcon(R.drawable.icon) will replace icon with name of existing icon from project
                        //set three option buttons
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Do nothing
                            }
                        })//end

                        .create();
                myInsertedDialogBox.show();
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(order_img, order_name, order_cat, order_size, order_price, provider_name, customer_name);
    }


}
