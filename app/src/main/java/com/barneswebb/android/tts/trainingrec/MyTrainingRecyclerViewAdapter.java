package com.barneswebb.android.tts.trainingrec;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barneswebb.android.tts.R;
import com.barneswebb.android.tts.trainingrec.TrainingFragment.OnListFragmentInteractionListener;
import com.barneswebb.android.tts.trainingrec.TrainingContent.TrainingItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link TrainingItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyTrainingRecyclerViewAdapter extends RecyclerView.Adapter<MyTrainingRecyclerViewAdapter.ViewHolder> {

    private final List<ExerciseSession> exerciseList;
    private final OnListFragmentInteractionListener mListener;

    public MyTrainingRecyclerViewAdapter(List<ExerciseSession> items, OnListFragmentInteractionListener listener) {
        exerciseList = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_training_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.exerciseSession = exerciseList.get(position);
        holder.mIdView.setText(         exerciseList.get(position).id+"");
        holder.mDateView.setText(       "Date: "+       exerciseList.get(position).excercise_date);
        holder.mDurationView.setText(   "Duration: " +  exerciseList.get(position).duration);
        holder.mCompltExView.setText(   "Programme: " + exerciseList.get(position).program);
        holder.mCommentView.setText(    exerciseList.get(position).comments);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.exerciseSession);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mDateView;
        public final TextView mCommentView;
        public final TextView mCompltExView;
        public final TextView mDurationView;
        public ExerciseSession exerciseSession;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.training_rec_id);
            mDateView = (TextView) view.findViewById(R.id.training_rec_date);
            mDurationView = (TextView) view.findViewById(R.id.training_rec_duration);
            mCompltExView = (TextView) view.findViewById(R.id.training_rec_exercz_cmplt);
            mCommentView = (TextView) view.findViewById(R.id.training_rec_comment);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCommentView.getText() + "'";
        }
    }
}
