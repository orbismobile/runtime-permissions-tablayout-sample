package pe.com.orbis.runtimepermissions_sample.view.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import pe.com.orbis.runtimepermissions_sample.R;
import pe.com.orbis.runtimepermissions_sample.view.fragment.GalleryFragment;


/**
 * Created by carlos on 22/06/16.
 * Alias: CarlitosDroid
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ItemViewHolder>{

    private List<String> listFilePaths;

    public GalleryAdapter(GalleryFragment galleryFragment, List<String> listFilePaths) {
        this.listFilePaths = listFilePaths;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_gallery_rcv, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        Uri uri = Uri.fromFile(new File(listFilePaths.get(position)));

        Picasso.with(holder.imgGallery.getContext()).load(uri)
                .resize(holder.imgGallery.getContext().getResources().getInteger(R.integer.image_gallery_best_size),
                        holder.imgGallery.getContext().getResources().getInteger(R.integer.image_gallery_best_size))
                .centerCrop().into(holder.imgGallery);
    }

    @Override
    public int getItemCount() {
        return listFilePaths.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        ImageView imgGallery;

        public ItemViewHolder(View itemView) {
            super(itemView);
            imgGallery = (ImageView) itemView.findViewById(R.id.imgGallery);
        }
    }
}
