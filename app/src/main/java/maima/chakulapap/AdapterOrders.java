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

public class AdapterOrders extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<DataFood> data= Collections.emptyList();
    DataFood current;
    int currentPos=0;
    String msg;
    ProgressDialog progressDialog;
    // create constructor to innitilize context and data sent from MainActivity
    public AdapterOrders(Context context, List<DataFood> data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data=data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.container_orders, parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder= (MyHolder) holder;
        final DataFood current=data.get(position);
        myHolder.textFishName.setText(current.fishName);
        myHolder.textSize.setText("Size: " + current.sizeName);
        myHolder.textType.setText("Category: " + current.catName);
        myHolder.textPrice.setText("Ksh. " + current.price);
        myHolder.textCustomer.setText("Customer: " + current.customerName);
        myHolder.textPrice.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        final String order_status = "in_transit";

        //Our order button listener
        myHolder.order_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(final View view){
                //Order Button click event
                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(view.getContext())
                        //set message, title, and icon
                        .setTitle("Confirm order")
                        .setMessage("Are you sure you want to accept order "+ current.fishName + "?")
                        //.setIcon(R.drawable.icon) will replace icon with name of existing icon from project
                        //set two option buttons
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                progressDialog = new ProgressDialog(context);
                                progressDialog.setCancelable(false);
                                progressDialog.setTitle("Processing...");
                                progressDialog.setMessage("Please wait...");
                                progressDialog.show();

                                //Send the order details to the orders table in db
                                updateOrders(Integer.toString(position+1), order_status, current.fishName);

                                Snackbar.make(view, "redirect to maps activity showing distance btwn the two...", Snackbar.LENGTH_LONG).show();

                            }
                        })//setPositiveButton


                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                Snackbar.make(view, "Delivery declined", Snackbar.LENGTH_LONG).show();
                            }
                        })//setNegativeButton

                        .create();
                myQuittingDialogBox.show();

            }
        });

        //Our decline button listener
        myHolder.decline_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(final View view){
                //Order Button click event
                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(view.getContext())
                        //set message, title, and icon
                        .setTitle("Decline order")
                        .setMessage("Are you sure you want to decline order "+ current.fishName + "?")
                        //.setIcon(R.drawable.icon) will replace icon with name of existing icon from project
                        //set two option buttons
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                progressDialog = new ProgressDialog(context);
                                progressDialog.setCancelable(false);
                                progressDialog.setTitle("Processing...");
                                progressDialog.setMessage("Please wait...");
                                progressDialog.show();

                                updateOrders(Integer.toString(position+1), "declined", current.fishName);

                            }
                        })//setPositiveButton


                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                Snackbar.make(view, "Delivery in progress...", Snackbar.LENGTH_LONG).show();
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
        TextView textCustomer;
        Button order_button;
        Button decline_button;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textFishName= (TextView) itemView.findViewById(R.id.textFishName);
            ivFish= (ImageView) itemView.findViewById(R.id.ivFish);
            textSize = (TextView) itemView.findViewById(R.id.textSize);
            textType = (TextView) itemView.findViewById(R.id.textType);
            textPrice = (TextView) itemView.findViewById(R.id.textPrice);
            textCustomer = (TextView) itemView.findViewById(R.id.textCustomer);
            order_button = (Button) itemView.findViewById(R.id.order_button);
            decline_button = (Button) itemView.findViewById(R.id.decline_button);
        }

    }

    /**
     * Here we take in 6 string parameters that we will push to our orders database
     */
    protected void updateOrders(final String position, final String order_status, final String order_name){
        class  SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            public String doInBackground(String... params) { //Will be executed when we execute AsyncTask

                String paramPosition = params[0];
                String paramOrder_status = params[1];

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id",position));
                nameValuePairs.add(new BasicNameValuePair("order_status", order_status));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://dev.oscorpltd.com/UpdateOrders.php"
                    );

                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpClient.execute(httpPost);
                    HttpEntity entity = response.getEntity();
                }
                catch (ClientProtocolException e){

                }

                catch (IOException e){

                }
                return order_name;
            }

            protected void onPostExecute(String result){ //Will execute after the execution of doInBackground
                super.onPostExecute(result);
                //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                progressDialog.hide();
                AlertDialog myInsertedDialogBox = new AlertDialog.Builder(context)
                        //set message, title, and icon
                        .setTitle(result+" request sent")
                        .setMessage("Your " + result +  " request has been sent")
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
        sendPostReqAsyncTask.execute(order_status, position);
    }


}
