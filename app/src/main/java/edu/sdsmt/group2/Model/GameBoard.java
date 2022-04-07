package edu.sdsmt.group2.Model;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.sdsmt.group2.Control.WelcomeActivity;

public class GameBoard {
    private static final String PLAYER_NAMES ="GameBoard.playerNames" ;
    private static final String PLAYER_SCORES ="GameBoard.playerScores" ;
    private static final String CURRENT_PLAYER_ID = "GameBoard.currentPlayerScore";
    private final DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference().child("game");
    private final ArrayList<Collectable> collectables = new ArrayList<>();
    private Player currentPlayer;
    private final ArrayList<Player> players = new ArrayList<>();
    private final static String REL_LOCATIONS = "GameBoard.relLocations";
    private final static String LOCATIONS = "GameBoard.locations";
    private final static String IDS = "GameBoard.ids";
    private int rounds;
    private final Context context;

    public GameBoard(Context context) {
        this.context = context;

        for (int i = 0; i < 21; i++)
            collectables.add(new Collectable(context, i, 0.2f));

        gameRef.child("collectables").setValue(collectables);
        gameRef.child("p1Score").setValue(0);
        gameRef.child("p2Score").setValue(0);
        gameRef.child("round").setValue(5);

        gameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                players.get(0).setScore(snapshot.child("p1Score").getValue(Integer.class));
                players.get(1).setScore(snapshot.child("p2Score").getValue(Integer.class));
                rounds = snapshot.child("round").getValue(Integer.class);

                int i = 0;
                for (DataSnapshot obj : snapshot.child("collectables").getChildren()) {
                    Collectable collectable = new Collectable(context, obj.child("id").getValue(Integer.class), 0.2f);
                    collectable.setX(obj.child("x").getValue(Float.class));
                    collectable.setX(obj.child("y").getValue(Float.class));
                    collectable.setX(obj.child("relX").getValue(Float.class));
                    collectable.setX(obj.child("relY").getValue(Float.class));
                    collectable.setShuffle(false);
                    collectables.set(i, collectable);
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public ArrayList<Collectable> getCollectables() {
        return collectables;
    }

    public void capture(CaptureObject capture) {
        ArrayList<Collectable> collected = capture.getContainedCollectables(collectables);
        for (Collectable c : collected)
            collectables.remove(c);

        int otherPlayer = currentPlayer.getId() % 2 + 1;

        currentPlayer.incScore(collected.size());
        gameRef.child("collectables").setValue(collectables);
        gameRef.child("p" + currentPlayer.getId() + "Score").setValue(currentPlayer.getScore());
        gameRef.child("round").setValue(getRounds());
        gameRef.child("nextPlayer").setValue(otherPlayer);

        gameRef.child("p" + otherPlayer + "Score").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                players.get(otherPlayer - 1).setScore(snapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        if (currentPlayer.getId() == 2)
            rounds--;
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

    public boolean isEndGame(){ return rounds <= 0 || collectables.isEmpty(); }

    public void addPlayer(String name, int id) {
        players.add(new Player(name, id));
    }

    public void setDefaultPlayer() {
        if (!players.isEmpty())
            currentPlayer = players.get(0);
    }

    public void setPlayer(int player) {
        currentPlayer = players.get(player - 1);
    }

    public void setRounds(int r) { rounds = r; }

    public int getRounds() { return rounds; }

    public int getCurrentPlayerId() { return currentPlayer.getId(); }

    public String getPlayer1Score() { return String.valueOf(players.get(0).getScore()); }

    public String getPlayer2Score() { return String.valueOf(players.get(1).getScore()); }

    public String getPlayer1Name() { return players.get(0).getName(); }

    public String getPlayer2Name() { return players.get(1).getName(); }
}
