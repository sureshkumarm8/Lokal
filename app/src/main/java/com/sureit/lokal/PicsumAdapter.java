package com.sureit.lokal;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class PicsumAdapter extends RecyclerView.Adapter<PicsumAdapter.ViewHolder> {
    private List<ImageList> imageLists;
    private Context context;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id=1;
    private String filename=" ";

    public PicsumAdapter(List<ImageList> imageLists, Context context) {
        this.imageLists = imageLists;
        this.context = context;

    }

    @NonNull
    @Override
    public PicsumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.picsum_item,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PicsumAdapter.ViewHolder viewHolder, final int i) {

       filename = imageLists.get(i).getImageUrl();
        viewHolder.picsumImageView.setText(filename);
        viewHolder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                viewHolder.downloadBtn.setText("Downloading.....");
                new LoadImage(viewHolder).execute(imageLists.get(i).getPostUrl()+"/download");
                downloadNotificaton();

            }
        });
    }

    private void downloadNotificaton() {
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("File Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.lokal);
        // Start a the operation in a background thread
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int incr;
                        // Do the "lengthy" operation 20 times
                        for (incr = 0; incr <= 100; incr+=5) {
                            // Sets the progress indicator to a max value, the current completion percentage and "determinate" state
                            mBuilder.setProgress(100, incr, false);
                            // Displays the progress bar for the first time.
                            mNotifyManager.notify(id, mBuilder.build());
                            // Sleeps the thread, simulating an operation
                            try {
                                // Sleep for 1 second
                                Thread.sleep(1*1000);
                            } catch (InterruptedException e) {
                                Log.d("TAG", "sleep failure");
                            }
                        }
                        // When the loop is finished, updates the notification
                        mBuilder.setContentText("Download completed")
                                // Removes the progress bar
                                .setProgress(0,0,false);
                        mNotifyManager.notify(id, mBuilder.build());
                    }
                }
                // Starts the thread by calling the run() method in its Runnable
        ).start();

    }

    @Override
    public int getItemCount() {
        return imageLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView picsumImageView;
        Button downloadBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            picsumImageView=itemView.findViewById(R.id.picsumimageView);
            downloadBtn=itemView.findViewById(R.id.downloadbutton);
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        private ViewHolder viewHolder;

        public LoadImage(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Bitmap doInBackground(String... args) {
            //1 url
            Bitmap bitmap = null;
            if(args.length == 1){
                Log.i("doInBack 1","length = 1 ");
                try {
                    bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap image) {
            if(image != null){
                try {
                String myfile= Environment.getExternalStorageDirectory()+"/Lokal/"+filename+".png";
                File f=new File(myfile);
                if(f.exists())
                    f.delete();
                f.createNewFile();
                Bitmap bitmap = image;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
                viewHolder.downloadBtn.setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
