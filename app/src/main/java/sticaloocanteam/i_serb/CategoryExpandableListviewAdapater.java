package sticaloocanteam.i_serb;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.List;

public class CategoryExpandableListviewAdapater extends BaseExpandableListAdapter {

    private Context _context;

    private List<String> _listDataHeaderIcon;// header icons
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, String[][]> _listDataChild;

    public CategoryExpandableListviewAdapater(Context context, List<String> _listDataHeaderIcon,List<String> listDataHeader,HashMap<String, String[][]> _listDataChild ) {
        this._context = context;
        this._listDataHeaderIcon = _listDataHeaderIcon;
        this._listDataHeader = listDataHeader;
        this._listDataChild = _listDataChild;

    }


    @Override
    public View getGroupView(int groupPosition, boolean b, View convertView, ViewGroup viewGroup) {

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.category_listheader, null);
        }

        ImageView imgV_icon = convertView.findViewById(R.id.imgListHeader);
        Glide.with(_context).load(_listDataHeaderIcon.get(groupPosition)).apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder)).into(imgV_icon);

        TextView lblListHeader =  convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup viewGroup) {

            final String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.category_listitem, null);
            }

            TextView txtListChild =  convertView.findViewById(R.id.lblListItem);
            txtListChild.setText(childText);
            return convertView;
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(this._listDataChild.get(this._listDataHeader.get(groupPosition))==null){
            Toast.makeText(_context, "Category has no sub-categories", Toast.LENGTH_SHORT).show();
            return 0;
        }
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        String arr [][]  = this._listDataChild.get(this._listDataHeader.get(groupPosition));
        return arr[childPosititon][1];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosititon) {
        return childPosititon;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
