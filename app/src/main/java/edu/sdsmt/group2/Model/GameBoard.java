package edu.sdsmt.group2.Model;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.sdsmt.group2.Control.GameBoardActivity;
import edu.sdsmt.group2.R;
import edu.sdsmt.group2.View.GameBoardView;

public class GameBoard {
    private static final String PLAYER_NAMES ="GameBoard.playerNames" ;
    private static final String PLAYER_SCORES ="GameBoard.playerScores" ;
    private static final String CURRENT_PLAYER_ID = "GameBoard.currentPlayerScore";
    private final DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference().child("game2");
    private final ArrayList<Collectable> collectables = new ArrayList<>();
    private Player currentPlayer;
    private final ArrayList<Player> players = new ArrayList<>();
    private final static String REL_LOCATIONS = "GameBoard.relLocations";
    private final static String LOCATIONS = "GameBoard.locations";
    private final static String IDS = "GameBoard.ids";
    private int rounds;
    private final Context context;
    private final ValueEventListener collects;
    private ValueEventListener next, score1, score2, round;

    public GameBoard(Context context, GameBoardView gbv) {
        this.context = context;

        for (int i = 0; i < 21; i++)
            collectables.add(new Collectable(context, i, 0.2f));

        gameRef.child("collectables").setValue(collectables);
        gameRef.child("nextPlayer").setValue(1);
        gameRef.child("p1Score").setValue(0);
        gameRef.child("p2Score").setValue(0);
        gameRef.child("round").setValue(5);

        collects = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                collectables.clear();
                for (DataSnapshot obj : snapshot.getChildren()) {
                    Collectable collectable = new Collectable(context, obj.child("id").getValue(Integer.class), 0.2f);
                    collectable.setX(obj.child("x").getValue(Float.class));
                    collectable.setY(obj.child("y").getValue(Float.class));
                    collectable.setRelX(obj.child("relX").getValue(Float.class));
                    collectable.setRelY(obj.child("relY").getValue(Float.class));
                    collectable.setShuffle(obj.child("shuffle").getValue(boolean.class));
                    collectables.add(collectable);
                }
                gbv.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        gameRef.child("collectables").addValueEventListener(collects);
    }

    public void init(GameBoardActivity gba) {
        next = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gba.findViewById(R.id.button).setEnabled(snapshot.getValue(Integer.class) == currentPlayer.getId());
                gba.updateGUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        score1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                players.get(0).setScore(snapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        score2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                players.get(1).setScore(snapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        round = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rounds = snapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        gameRef.child("nextPlayer").addValueEventListener(next);
        gameRef.child("p1Score").addValueEventListener(score1);
        gameRef.child("p2Score").addValueEventListener(score2);
        gameRef.child("round").addValueEventListener(round);
    }

    public void destroy() {
        gameRef.child("collectables").removeEventListener(collects);
        gameRef.child("nextPlayer").removeEventListener(next);
        gameRef.child("p1Score").removeEventListener(score1);
        gameRef.child("p2Score").removeEventListener(score2);
        gameRef.child("round").removeEventListener(round);
    }

    public ArrayList<Collectable> getCollectables() {
        return collectables;
    }

    public void capture(CaptureObject capture) {
        ArrayList<Collectable> collected = capture.getContainedCollectables(collectables);
        for (Collectable c : collected)
            collectables.remove(c);

        if (currentPlayer.getId() == 2)
            rounds--;

        currentPlayer.incScore(collected.size());
        gameRef.child("collectables").setValue(collectables);
        gameRef.child("p" + currentPlayer.getId() + "Score").setValue(currentPlayer.getScore());
        gameRef.child("round").setValue(rounds);
        gameRef.child("nextPlayer").setValue(currentPlayer.getId() % 2 + 1);
    }

    public void saveInstanceState( Bundle bundle) {
        float [] relLocations = new float[collectables.size() * 2];
        float [] locations = new float[collectables.size() * 2];
        int [] ids = new int[collectables.size()];

        int [] playerScores = new int[players.size()];
        String [] playerNames = new String[players.size()];
        for (int i = 0; i < collectables.size(); i++) {
            Collectable collectable = collectables.get(i);
            relLocations[i * 2] = collectable.getRelX();
            relLocations[i * 2 + 1] = collectable.getRelY();
            locations[i * 2] = collectable.getX();
            locations[i * 2 + 1] = collectable.getY();
            ids[i] = collectable.getId();
        }
        for(int i = 0; i < players.size(); i ++) {
            playerNames[i] = players.get(i).getName();
            playerScores[i] = players.get(i).getScore();
        }
        bundle.putFloatArray(REL_LOCATIONS, relLocations);
        bundle.putFloatArray(LOCATIONS, locations);
        bundle.putIntArray(IDS,  ids);
        bundle.putIntArray(PLAYER_SCORES, playerScores);
        bundle.putStringArray(PLAYER_NAMES, playerNames);
        bundle.putInt(CURRENT_PLAYER_ID, currentPlayer.getId());
    }

    public void loadInstanceState(Bundle bundle) {
        float [] relLocations = bundle.getFloatArray(REL_LOCATIONS);
        float [] locations = bundle.getFloatArray(LOCATIONS);
        int [] ids = bundle.getIntArray(IDS);
        String[] playerNames = bundle.getStringArray(PLAYER_NAMES);
        int[] playerScores = bundle.getIntArray(PLAYER_SCORES);
        int id = bundle.getInt(CURRENT_PLAYER_ID);

        collectables.clear();

        for (int i = 0; i < ids.length; i++) {
            Collectable collectable = new Collectable(context, ids[i], 0.2f);
            collectable.setRelX(relLocations[i*2]);
            collectable.setRelY(relLocations[i*2+1]);
            collectable.setX(locations[i*2]);
            collectable.setY(locations[i*2+1]);
            collectable.setShuffle(false);
            collectables.add(collectable);
        }
        for(int i = 0; i < playerNames.length; i++)
        {
            players.add(new Player(playerNames[i], i));
            players.get(i).incScore(playerScores[i]);
        }

        currentPlayer = new Player(players.get(id).getName(), id);
    }

    public boolean isEndGame() { return rounds <= 0 || collectables.isEmpty(); }

    public void addPlayer(String name, int id) {
        players.add(new Player(name, id));
    }

    public void setPlayer(int player) {
        currentPlayer = players.get(player - 1);
    }

    public void setRounds(int r) {
        rounds = r;
    }

    public int getRounds() { return rounds; }

    public String getPlayer1Score() { return String.valueOf(players.get(0).getScore()); }

    public String getPlayer2Score() { return String.valueOf(players.get(1).getScore()); }

    public String getPlayer1Name() { return players.get(0).getName(); }

    public String getPlayer2Name() { return players.get(1).getName(); }
}
