package app.intelehealth.client.activities.questionNodeActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.intelehealth.client.R;
import app.intelehealth.client.activities.familyHistoryActivity.FamilyHistoryActivity;
import app.intelehealth.client.activities.pastMedicalHistoryActivity.PastMedicalHistoryActivity;
import app.intelehealth.client.activities.physcialExamActivity.PhysicalExamActivity;
import app.intelehealth.client.app.IntelehealthApplication;
import app.intelehealth.client.knowledgeEngine.Node;
import app.intelehealth.client.knowledgeEngine.PhysicalExam;

/**
 * Created by Sagar Shimpi
 * Github - TheSeasApps
 */
public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ChipsAdapterViewHolder> {

    LayoutInflater layoutInflater;
    Context context;
    Node currentNode;
    int pos;
    RecyclerView recyclerView;
    FabClickListener _mListener;
    String _mCallingClass;
    boolean isAssociateSym;

    public void updateNode(Node currentNode) {
        this.currentNode = currentNode;
        notifyDataSetChanged();
    }

    boolean isChildNeedRefresh = false;

    public void refreshChildAdapter() {
        this.isChildNeedRefresh = true;
    }

    public interface FabClickListener {
        void fabClickedAtEnd();

        void onChildListClickEvent(int groupPos, int childPos, int physExamPos);
    }

    public QuestionsAdapter(Context _context, Node node, RecyclerView _rvQuestions, String callingClass,
                            FabClickListener _mListener, boolean isAssociateSym) {
        this.context = _context;
        this.currentNode = node;
        this.recyclerView = _rvQuestions;
        this._mCallingClass = callingClass;
        this._mListener = _mListener;
        this.isAssociateSym = isAssociateSym;

    }

    PhysicalExam physicalExam;

    public QuestionsAdapter(Context _context, PhysicalExam node, RecyclerView _rvQuestions, String callingClass,
                            FabClickListener _mListener, boolean isAssociateSym) {
        this.context = _context;
        this.physicalExam = node;
        this.recyclerView = _rvQuestions;
        this._mCallingClass = callingClass;
        this._mListener = _mListener;
        this.isAssociateSym = isAssociateSym;

    }

    @Override
    public QuestionsAdapter.ChipsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.quesionnode_list_item, parent, false);
        return new ChipsAdapterViewHolder(row);
    }

    @Override
    public void onBindViewHolder(QuestionsAdapter.ChipsAdapterViewHolder holder, int position) {
        Node _mNode;
        if (_mCallingClass.equalsIgnoreCase(PhysicalExamActivity.class.getSimpleName())) {
            _mNode = physicalExam.getExamNode(position).getOption(0);
            final String parent_name = physicalExam.getExamParentNodeName(position);
            String nodeText = parent_name + " : " + _mNode.findDisplay();

            holder.physical_exam_text_view.setText(nodeText);
            holder.physical_exam_text_view.setVisibility(View.GONE);
            if (_mNode.isAidAvailable()) {
                String type = _mNode.getJobAidType();
                if (type.equals("video")) {
                    holder.physical_exam_image_view.setVisibility(View.GONE);
                } else if (type.equals("image")) {
                    holder.physical_exam_image_view.setVisibility(View.VISIBLE);
                    String drawableName = "physicalExamAssets/" + _mNode.getJobAidFile() + ".jpg";
                    try {
                        // get input stream
                        InputStream ims = context.getAssets().open(drawableName);
                        // load image as Drawable
                        Drawable d = Drawable.createFromStream(ims, null);
                        // set image to ImageView
                        holder.physical_exam_image_view.setImageDrawable(d);
                        holder.physical_exam_image_view.setMinimumHeight(500);
                        holder.physical_exam_image_view.setMinimumWidth(500);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        holder.physical_exam_image_view.setVisibility(View.GONE);
                    }
                } else {
                    holder.physical_exam_image_view.setVisibility(View.GONE);
                }
            } else {
                holder.physical_exam_image_view.setVisibility(View.GONE);
            }
            holder.tvQuestion.setText(_mNode.findDisplay());
        } else {
            _mNode = currentNode;
            if (isAssociateSym && currentNode.getOptionsList().size() == 1) {
                holder.tvQuestion.setText(_mNode.getOptionsList().get(0).findDisplay());
            } else {
                holder.tvQuestion.setText(_mNode.getOptionsList().get(position).findDisplay());
            }
            holder.physical_exam_image_view.setVisibility(View.GONE);
            holder.physical_exam_text_view.setVisibility(View.GONE);
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (_mCallingClass.equalsIgnoreCase(QuestionNodeActivity.class.getSimpleName())) {
                    ((QuestionNodeActivity) context).AnimateView(holder.ivAyu);
                    ((QuestionNodeActivity) context).AnimateView(holder.tvQuestion);
                    ((QuestionNodeActivity) context).AnimateView(holder.tvSwipe);
                    ((QuestionNodeActivity) context).bottomUpAnimation(holder.rvChips);
                } else if (_mCallingClass.equalsIgnoreCase(PastMedicalHistoryActivity.class.getSimpleName())) {
                    ((PastMedicalHistoryActivity) context).AnimateView(holder.ivAyu);
                    ((PastMedicalHistoryActivity) context).AnimateView(holder.tvQuestion);
                    ((PastMedicalHistoryActivity) context).AnimateView(holder.tvSwipe);
                    ((PastMedicalHistoryActivity) context).bottomUpAnimation(holder.rvChips);
                } else if (_mCallingClass.equalsIgnoreCase(FamilyHistoryActivity.class.getSimpleName())) {
                    ((FamilyHistoryActivity) context).AnimateView(holder.ivAyu);
                    ((FamilyHistoryActivity) context).AnimateView(holder.tvQuestion);
                    ((FamilyHistoryActivity) context).AnimateView(holder.tvSwipe);
                    ((FamilyHistoryActivity) context).bottomUpAnimation(holder.rvChips);
                } else if (_mCallingClass.equalsIgnoreCase(PhysicalExamActivity.class.getSimpleName())) {
                    ((PhysicalExamActivity) context).AnimateView(holder.ivAyu);
                    ((PhysicalExamActivity) context).AnimateView(holder.tvQuestion);
                    ((PhysicalExamActivity) context).AnimateView(holder.tvSwipe);
                    ((PhysicalExamActivity) context).bottomUpAnimation(holder.rvChips);
                    ((PhysicalExamActivity) context).AnimateView(holder.physical_exam_image_view);
                    ((PhysicalExamActivity) context).AnimateView(holder.physical_exam_text_view);
                }

            }
        });



        if(getItemCount()-1 == 0){
            holder.tv_swipe_view.setVisibility(View.GONE);
        }else{
            holder.tv_swipe_view.setVisibility(View.VISIBLE);
        }


        if (position == getItemCount() - 1) {
            holder.tvSwipe.setText(context.getString(R.string.swipe_down));
        } else if (position != 0) {
            holder.tvSwipe.setText(context.getString(R.string.swipe_down_to_return));
            // holder.tvSwipe.setVisibility(View.VISIBLE);
        } else {
            holder.tvSwipe.setText(context.getString(R.string.swipe_up));
            // holder.tvSwipe.setVisibility(View.GONE);
        }

        if (position == getItemCount() - 1) {
            holder.fab.setVisibility(View.VISIBLE);
        } else {
            holder.fab.setVisibility(View.INVISIBLE);
        }


        holder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _mListener.fabClickedAtEnd();
            }
        });

        if (isChildNeedRefresh) {
            holder.rvChips.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if (_mCallingClass.equalsIgnoreCase(PhysicalExamActivity.class.getSimpleName())) {
            return physicalExam.getTotalNumberOfExams();
        } else {
            if (isAssociateSym && currentNode.getOptionsList().size() == 1) {
                List<Node> nodeList = currentNode.getOptionsList().get(0).getOptionsList();
                if (nodeList.size() > 5) {
                    List<List<Node>> spiltList = Lists.partition(currentNode.getOptionsList().get(0).getOptionsList(), 5);
                    return spiltList.size();
                } else {
                    return currentNode.getOptionsList().size();
                }
            } else {
                return currentNode.getOptionsList().size();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        pos = position;
        return position;
    }

    public class ChipsAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tvQuestion, physical_exam_text_view;
        TextView tvSwipe;
        ImageView ivAyu, physical_exam_image_view;
        RecyclerView rvChips;
        FloatingActionButton fab;
        ComplaintNodeListAdapter chipsAdapter;
        RelativeLayout tv_swipe_view;


        public ChipsAdapterViewHolder(View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tv_complaintQuestion);
            rvChips = itemView.findViewById(R.id.rv_chips);
            fab = itemView.findViewById(R.id.fab);
            tv_swipe_view = itemView.findViewById(R.id.tv_swipe_view);
              tvSwipe = itemView.findViewById(R.id.tv_swipe);
            physical_exam_text_view = itemView.findViewById(R.id.physical_exam_text_view);
            physical_exam_image_view = itemView.findViewById(R.id.physical_exam_image_view);


            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
            rvChips.setLayoutManager(linearLayoutManager);
            rvChips.setHasFixedSize(true);
            //rvChips.setItemAnimator(new DefaultItemAnimator());
            rvChips.setNestedScrollingEnabled(true);

            Node groupNode;
            List<Node> chipList = new ArrayList<>();
            if (_mCallingClass.equalsIgnoreCase(PhysicalExamActivity.class.getSimpleName())) {
                groupNode = physicalExam.getExamNode(pos).getOption(0);
                for (int i = 0; i < groupNode.getOptionsList().size(); i++) {
                    chipList.add(groupNode.getOptionsList().get(i));
                }
            } else {
                groupNode = currentNode;
                if (isAssociateSym && currentNode.getOptionsList().size() == 1) {
                    int childOptionCount = currentNode.getOptionsList().get(0).getOptionsList().size();
                    if (childOptionCount > 5) {
                        List<List<Node>> spiltList = Lists.partition(currentNode.getOptionsList().get(0).getOptionsList(), 5);
                        chipList.addAll(spiltList.get(pos));
                    } else {
                        Node node = currentNode.getOptionsList().get(0);
                        for (int i = 0; i < node.getOptionsList().size(); i++) {
                            chipList.add(node.getOptionsList().get(i));
                        }
                    }
                } else {
                    Node node = currentNode.getOptionsList().get(pos);
                    for (int i = 0; i < node.getOptionsList().size(); i++) {
                        chipList.add(node.getOptionsList().get(i));
                    }
                }
            }


            int groupPos = (_mCallingClass.equalsIgnoreCase(PhysicalExamActivity.class.getSimpleName()) || (isAssociateSym && currentNode.getOptionsList().size() == 1)) ? 0 : pos;
            chipsAdapter = new ComplaintNodeListAdapter(context, chipList, groupNode, groupPos, _mListener, _mCallingClass, pos);
            rvChips.setAdapter(chipsAdapter);

        }
    }


    class ComplaintNodeListAdapter extends RecyclerView.Adapter<ComplaintNodeListAdapter.ItemViewHolder> {
        private static final String TAG = "CNodeListAdapter";

        private Context mContext;
        private int layoutResourceID;
        private ImmutableList<Node> mNodes;
        private List<Node> mNodesFilter;
        private Node mGroupNode;
        private int mGroupPos;
        private QuestionsAdapter.FabClickListener _mListener;
        String _mCallingClass;
        private int physExamNodePos;

        public ComplaintNodeListAdapter(Context context, List<Node> nodes, Node groupNode, int groupPos,
                                        QuestionsAdapter.FabClickListener listener, String callingClass, int nodePos) {
            this.mContext = context;
            this.mNodesFilter = nodes;
            this.mNodes = ImmutableList.copyOf(mNodesFilter);
            mGroupNode = groupNode;
            mGroupPos = groupPos;
            this._mListener = listener;
            this._mCallingClass = callingClass;
            this.physExamNodePos = nodePos;
        }


        @NonNull
        @Override
        public ComplaintNodeListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.layout_chip, parent, false);
            return new ComplaintNodeListAdapter.ItemViewHolder(row);
        }

        @Override
        public void onBindViewHolder(@NonNull ComplaintNodeListAdapter.ItemViewHolder itemViewHolder, int position) {
            final Node thisNode = mNodesFilter.get(position);
            itemViewHolder.mChip.setText(thisNode.findDisplay());

            Node groupNode = mGroupNode.getOption(mGroupPos);

            if ((groupNode.getText().equalsIgnoreCase("Associated symptoms") && thisNode.isNoSelected()) || thisNode.isSelected()) {
                itemViewHolder.mChip.setCloseIconVisible(true);
                itemViewHolder.mChip.setChipBackgroundColor((ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorAccent))));
                itemViewHolder.mChip.setTextColor((ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.white))));
            } else {
                itemViewHolder.mChip.setCloseIconVisible(false);
                itemViewHolder.mChip.setChipBackgroundColor((ColorStateList.valueOf(ContextCompat.getColor(mContext, android.R.color.transparent))));
                itemViewHolder.mChip.setTextColor((ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.primary_text))));
            }
            itemViewHolder.mChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (groupNode.getText().equalsIgnoreCase("Associated symptoms")) {
                        MaterialAlertDialogBuilder confirmDialog = new MaterialAlertDialogBuilder(context);
                        confirmDialog.setTitle(R.string.have_symptom);
                        confirmDialog.setCancelable(false);
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View convertView = layoutInflater.inflate(R.layout.list_expandable_item_radio, null);
                        confirmDialog.setView(convertView);
                        RadioButton radio_yes = convertView.findViewById(R.id.radio_yes);
                        RadioButton radio_no = convertView.findViewById(R.id.radio_no);
                        confirmDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = confirmDialog.create();
                        radio_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                thisNode.setNoSelected(false);
                                List<Node> childNode = mGroupNode.getOptionsList().get(mGroupPos).getOptionsList();
                                int indexOfCheckedNode = childNode.indexOf(thisNode);
                                _mListener.onChildListClickEvent(mGroupPos, indexOfCheckedNode, physExamNodePos);
                                notifyDataSetChanged();
                                if (alertDialog != null) {
                                    alertDialog.dismiss();
                                }

                            }
                        });

                        radio_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                thisNode.setNoSelected(true);
                                thisNode.setUnselected();
                                notifyDataSetChanged();
                                if (alertDialog != null) {
                                    alertDialog.dismiss();
                                }
                            }
                        });

                        switch (_mCallingClass) {

                            case "ComplaintNodeActivity":
                                if (thisNode.isSelected()) {
                                    radio_yes.setChecked(true);
                                } else {
                                    radio_no.setChecked(true);
                                }
                                break;
                            default:
                                if (thisNode.isSelected()) {
                                    radio_yes.setChecked(true);
                                } else {
                                    if (thisNode.isNoSelected()) {
                                        radio_no.setChecked(true);
                                    } else {
                                        radio_no.setChecked(false);
                                    }
                                }
                                break;
                        }

                        alertDialog.show();
                        IntelehealthApplication.setAlertDialogCustomTheme(context,alertDialog);

                    } else {
                        //thisNode.toggleSelected();
                        int indexOfCheckedNode;
                        if (_mCallingClass.equalsIgnoreCase(PhysicalExamActivity.class.getSimpleName())) {
                            indexOfCheckedNode = position;
                        } else {
                            List<Node> childNode = mGroupNode.getOptionsList().get(mGroupPos).getOptionsList();
                            indexOfCheckedNode = childNode.indexOf(thisNode);
                        }
                        _mListener.onChildListClickEvent(mGroupPos, indexOfCheckedNode, physExamNodePos);
                        notifyDataSetChanged();
                    }

                }
            });

            itemViewHolder.mChip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //thisNode.toggleSelected();
                    if ((groupNode.getText().equalsIgnoreCase("Associated symptoms") && thisNode.isNoSelected())) {
                        thisNode.setNoSelected(false);
                        thisNode.toggleSelected();
                    }
                    int indexOfCheckedNode;
                    if (_mCallingClass.equalsIgnoreCase(PhysicalExamActivity.class.getSimpleName())) {
                        indexOfCheckedNode = position;
                    } else {
                        List<Node> childNode = mGroupNode.getOptionsList().get(mGroupPos).getOptionsList();
                        indexOfCheckedNode = childNode.indexOf(thisNode);
                    }
                    _mListener.onChildListClickEvent(mGroupPos, indexOfCheckedNode, physExamNodePos);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return (mNodesFilter != null ? mNodesFilter.size() : 0);
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            Chip mChip;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                mChip = itemView.findViewById(R.id.complaint_chip);
            }
        }


        public ImmutableList<Node> getmNodes() {
            return mNodes;
        }
    }


    public static <T> List<List<T>> partitionList(List<T> list, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Invalid  size to partition: " + chunkSize);
        }
        List<List<T>> chunkList = new ArrayList<>(list.size() / chunkSize);
        for (int i = 0; i < list.size(); i += chunkSize) {
            chunkList.add(list.subList(i, i + chunkSize >= list.size() ? list.size() - 1 : i + chunkSize));
        }
        return chunkList;
    }


}



