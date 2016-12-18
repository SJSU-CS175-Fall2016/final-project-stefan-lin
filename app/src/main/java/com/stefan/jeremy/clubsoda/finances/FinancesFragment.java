package com.stefan.jeremy.clubsoda.finances;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stefan.jeremy.clubsoda.R;
import com.stefan.jeremy.clubsoda.data.FinancesRecord;
import com.stefan.jeremy.clubsoda.data.User;
import com.stefan.jeremy.clubsoda.utils.Constants;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FinancesFragment extends Fragment {


  private OnFragmentInteractionListener mListener;
  private DatabaseReference mDatabase;

  private RecyclerView mRecyclerView;
  private FloatingActionButton mFab;
  private User mCurrUser;
  private TextView mDollarAmountTotal;

  public FinancesFragment() { /* Required empty constructor */ }
  public static FinancesFragment newInstance(User _mCurrUser) {
    FinancesFragment fragment = new FinancesFragment();
    Bundle args = new Bundle();
    args.putSerializable("user", _mCurrUser);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      mCurrUser = (User) savedInstanceState.getSerializable("user");
    } else {
      mCurrUser = (User) getArguments().getSerializable("user");
    }

    /* Fab */
    mFab = (FloatingActionButton) getActivity().findViewById(R.id.finances_FAB);
    mFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        openNewFinancesRecordActivity();
      }
    });

    /* Database */
    mDatabase = FirebaseDatabase.getInstance().getReference();
    mDollarAmountTotal = (TextView) getActivity().findViewById(R.id.finances_displayDollar);
    /* Check current balance */
    mDatabase.child("finances").child(mCurrUser.currClub).child("current-balance")
            .addValueEventListener(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                  NumberFormat formatter = NumberFormat.getCurrencyInstance();
                  if (dataSnapshot.getValue() instanceof Long) {
                    Long val = (Long) dataSnapshot.getValue();
                    String valStr = formatter.format(val);
                    mDollarAmountTotal.setText(valStr);
                  }
                  else if (dataSnapshot.getValue() instanceof Double) {
                    Double val = (Double) dataSnapshot.getValue();
                    String valStr = formatter.format(val);
                    mDollarAmountTotal.setText(valStr);
                  }
                }
              }
              @Override
              public void onCancelled(DatabaseError databaseError) { }
            }
    );
    /* Setup Recycler View */
    mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.finances_recycler);
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    mRecyclerView.setLayoutManager(layoutManager);

    /* RecyclerView Header (3rd party) */
    RecyclerViewHeader header = (RecyclerViewHeader) getActivity().findViewById(R.id.rvHeader);
    header.attachTo(mRecyclerView);

    /* Setup Recycler View with Firebase */
    DatabaseReference mRef = mDatabase.child("finances").child(mCurrUser.currClub).child("records");
    FirebaseRecyclerAdapter<FinancesRecord, MessageViewHolder> adapter =
      new FirebaseRecyclerAdapter<FinancesRecord, MessageViewHolder>(
            FinancesRecord.class,
            R.layout.single_finance_record,
            MessageViewHolder.class,
            mRef){
      @Override
      protected void populateViewHolder(MessageViewHolder viewHolder, FinancesRecord record, int position) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String strAmount = formatter.format(record.amount);
        strAmount = strAmount.substring(1);
        viewHolder.mAmount.setText(strAmount);
        if (record.amount > 0)
          viewHolder.mDollarSymbol.setTextColor(Color.parseColor(Constants.FINANCE_GREEN));
        else if (record.amount < 0)
          viewHolder.mDollarSymbol.setTextColor(Color.parseColor(Constants.FINANCE_RED));
        else
          viewHolder.mDollarSymbol.setTextColor(Color.GRAY);
        // Date is deprecated - how can we fix this?
        Date date = new Date(record.getTimestamp());
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
        String dateStr = sdf.format(date);
        viewHolder.mDescription.setText("Posted " + dateStr + "\nBy "
                                        + record.author+"\n\n"+record.description);
      }
    };
    /* Always set adapter! */
    mRecyclerView.setAdapter(adapter);

  }

  /* ViewHolder for RecyclerView */
  public static class MessageViewHolder extends RecyclerView.ViewHolder {
    TextView mDollarSymbol, mDescription, mAmount;
    public MessageViewHolder (View v) {
      super(v);
      mDollarSymbol = (TextView) v.findViewById(R.id.financeDollarSign);
      mAmount = (TextView) v.findViewById(R.id.financeAmount);
      mDescription = (TextView) v.findViewById(R.id.financeDescription);
    }
  }

  /* New Finance Record */
  private void openNewFinancesRecordActivity() {
    Intent intent = new Intent(getActivity(), NewFinancesRecordActivity.class);
    intent.putExtra("user", mCurrUser);
    startActivity(intent);
    getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
  }

  /* Overriden methods */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_finances, container, false);
  }

  @TargetApi(23)
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
              + " must implement OnFragmentInteractionListener");
    }
  }

  /*
   * Deprecated on API 23
   * Use onAttachToContext instead
   */
  @SuppressWarnings("deprecation")
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      onAttachToContext(activity);
    }
  }

  /*
   * Called when the fragment attaches to the context
   */
  protected void onAttachToContext(Context context) {
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
        + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onFinancesFragmentInteraction();
  }
}
