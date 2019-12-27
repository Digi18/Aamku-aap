package Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.aamku.Dashboard;
import com.app.aamku.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import Model.ProductsModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<ProductsModel> productList;
  //  private List<ProductsModel> selectedProductList = new ArrayList();

    FirebaseAuth fAuth;
    FirebaseUser user;
    int val;

    ProgressDialog prg;
    String id;


    private static final String URL = "https://aamku.herokuapp.com/order";

    public ProductAdapter(Context context, List<ProductsModel> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectpack_layout,parent,false);

        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductAdapter.ViewHolder holder, final int position) {



        prg = new ProgressDialog(context);
        prg.setMessage("Creating order");
        prg.setCancelable(false);

        final ProductsModel model = productList.get(position);

        holder.marketName.setText(model.getMarketName());
        holder.productNo.setText(model.getProductNo());
        holder.page.setText(model.getPage());
        holder.mrp.setText(model.getMrp());
        holder.innerPack.setText(model.getInnerPack());
        holder.outerPack.setText(model.getOuterPack());

        final List<String> qty = new ArrayList<>();
        qty.add("Select qty");
        qty.add("1");
        qty.add("2");
        qty.add("3");
        qty.add("4");
        qty.add("5");
        qty.add("6");
        qty.add("7");
        qty.add("8");
        qty.add("9");
        qty.add("10");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, qty);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        holder.qtySpinner.setAdapter(dataAdapter);

        holder.qtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    String item = adapterView.getItemAtPosition(i).toString();

                    if (!item.equals("Select qty")) {

                        int qty = Integer.parseInt(item);

                        int cost = Integer.parseInt(model.getMrp());

                         val = cost * qty;

                        holder.total.setText(String.valueOf(val));

                        Intent intent = new Intent("msg");
                        intent.putExtra("cost", String.valueOf(val));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        holder.order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

        /*    AlertDialog.Builder builder = new AlertDialog.Builder((Activity)context);
                    builder.setMessage("Confirm order");
                    builder.setCancelable(true);
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.cancel();
                        }
                    });
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {  */

                        //    prg.show();

                            fAuth = FirebaseAuth.getInstance();
                            user = fAuth.getCurrentUser();

                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(30, TimeUnit.SECONDS)
                                    .readTimeout(20,TimeUnit.SECONDS)
                                    .writeTimeout(30,TimeUnit.SECONDS)
                                    .build();

                            RequestBody formBody = new FormBody.Builder()
                                    .add("id",user.getUid())
                                    .add("market",model.getMarketName())
                                    .add("product",model.getProductNo())
                                    .add("cost", String.valueOf(val))
                                    .build();

                            Request request = new Request.Builder().post(formBody).url(URL).build();

                            client.newCall(request).enqueue(new Callback() {

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {

                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {

                                               String resp = response.body().string();

                                                if(resp.equals("Order generated")){

                                                    prg.dismiss();
                                                    Toast.makeText(context, resp, Toast.LENGTH_SHORT).show();
                                                    
                                                }

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                }

                                @Override
                                public void onFailure(@NotNull Call call, @NotNull final IOException e) {

                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            prg.dismiss();
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            });

//                        }
                //    });

                  //  AlertDialog alertDialog = builder.create();
                  //  alertDialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView  marketName,productNo,page,mrp,innerPack,outerPack,total;
        Spinner qtySpinner;
        Button order;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            order = itemView.findViewById(R.id.order);
            qtySpinner = itemView.findViewById(R.id.qtySpinner);
            marketName = itemView.findViewById(R.id.marketName);
            productNo = itemView.findViewById(R.id.productNo);
            page = itemView.findViewById(R.id.page);
            mrp = itemView.findViewById(R.id.mrp);
            innerPack = itemView.findViewById(R.id.innerPack);
            outerPack  = itemView.findViewById(R.id.outerPack);
            total = itemView.findViewById(R.id.total);
        }
    }
}
