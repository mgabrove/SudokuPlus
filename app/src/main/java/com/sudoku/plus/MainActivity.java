package com.sudoku.plus;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Klasa čelije sudoku ploče
    private class Cell {
        int value;
        int valueCorr;
        boolean fixed;

        Button bt;

        // Metoda za spremanje ispravne vrijednosti čelije
        public void setValueCorr(int valueCorr) {
            this.valueCorr = valueCorr;
        }

        // Konstruktor čelije
        public Cell(int _value, boolean surrender, Context ct) {
            value = _value;
            if(value != 0) fixed = true;
            else fixed = false;
            bt = new Button(ct);

            colourButton(bt);

            // Oboji čelije ovisno o stanju same čelije (startno popunjena ili unesena vrijednost) i same igre (predaja?)
            if(fixed) bt.setText(String.valueOf(value));
            else bt.setTextColor(Color.BLUE);
            if(surrender) bt.setTextColor(Color.RED);

            // OnClickListener na čeliji, ako je startno popunjena return, ako nije prikaži tipkovnicu unosa
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(fixed) return;
                    displayModal(bt, Cell.this);
                }
            });
        }
    }

    // Metoda provjere stanja ploče, je li do sada točna, i ako je je li riješen sudoku
    void snoop() {
        if(correct()) {
            tv.setText("");
            if(solved()) {
                tv.setText("You have solved the sudoku!!!");
                tv.setTextColor(Color.GREEN);
                if(!muted) {
                    // Muzika pobjede
                    MediaPlayer song = MediaPlayer.create(MainActivity.this, R.raw.success_fanfare_trumpets);
                    song.start();
                }
            }
        } else {
            tv.setText("There is duplicate digits!!!");
            tv.setTextColor(Color.GRAY);
        }
    }

    // Linearni layouti za tipkovnicu
    LinearLayout l1, l2, l3, l4, lM;

    // Metoda koja prikazuje izgrađenu modalnu tipkovnicu sa visoko-specijaliziranim funkcionalnostima za naš zadatak
    void displayModal(Button bt, Cell that) {
        // Izmijeni button koji je odabran
        ShapeDrawable shapedrawable = new ShapeDrawable();
        shapedrawable.setShape(new RectShape());
        shapedrawable.getPaint().setColor(Color.LTGRAY);
        shapedrawable.getPaint().setStyle(Paint.Style.FILL);
        bt.setBackground(shapedrawable);

        Button bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9, bt0, btX, btH;
        l1 = new LinearLayout(this);
        l2 = new LinearLayout(this);
        l3 = new LinearLayout(this);
        l4 = new LinearLayout(this);
        lM = new LinearLayout(this);
        lM.setOrientation(LinearLayout.VERTICAL);
        l1.setGravity(Gravity.CENTER);
        l2.setGravity(Gravity.CENTER);
        l3.setGravity(Gravity.CENTER);
        l4.setGravity(Gravity.CENTER);

        bt1 = new Button(this);
        bt2 = new Button(this);
        bt3 = new Button(this);
        bt4 = new Button(this);
        bt5 = new Button(this);
        bt6 = new Button(this);
        bt7 = new Button(this);
        bt8 = new Button(this);
        bt9 = new Button(this);
        bt0 = new Button(this);
        btX = new Button(this);
        btH = new Button(this);

        bt1.setText("1");
        bt2.setText("2");
        bt3.setText("3");
        bt4.setText("4");
        bt5.setText("5");
        bt6.setText("6");
        bt7.setText("7");
        bt8.setText("8");
        bt9.setText("9");
        bt0.setText("Remove");
        btX.setText("X");
        btH.setText("Hints: " + hint);

        l1.addView(bt1);
        l1.addView(bt2);
        l1.addView(bt3);
        l2.addView(bt4);
        l2.addView(bt5);
        l2.addView(bt6);
        l3.addView(bt7);
        l3.addView(bt8);
        l3.addView(bt9);
        l4.addView(btH);
        l4.addView(bt0);
        l4.addView(btX);

        lM.addView(l1);
        lM.addView(l2);
        lM.addView(l3);
        lM.addView(l4);

        // Stvori tipkovnicu kao AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");

        builder.setView(lM);

        final AlertDialog bd = builder.show();

        // Spriječi izlaz osim na unos kroz tipkovnicu
        bd.setCancelable(false);

        // Tipkovnica na dno ekrana
        bd.getWindow().setGravity(Gravity.BOTTOM);

        // Funkcionalnosti tipkovnice na OnClickListenerima
        bt1.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            colourButton(bt); bt.setText(String.valueOf(1)); that.value = 1; snoop(); bd.dismiss();
        }});
        bt2.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            colourButton(bt); bt.setText(String.valueOf(2)); that.value = 2; snoop(); bd.dismiss();
        }});
        bt3.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            colourButton(bt); bt.setText(String.valueOf(3)); that.value = 3; snoop(); bd.dismiss();
        }});
        bt4.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            colourButton(bt); bt.setText(String.valueOf(4)); that.value = 4; snoop(); bd.dismiss();
        }});
        bt5.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            colourButton(bt); bt.setText(String.valueOf(5)); that.value = 5; snoop(); bd.dismiss();
        }});
        bt6.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            colourButton(bt); bt.setText(String.valueOf(6)); that.value = 6; snoop(); bd.dismiss();
        }});
        bt7.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            colourButton(bt); bt.setText(String.valueOf(7)); that.value = 7; snoop(); bd.dismiss();
        }});
        bt8.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            colourButton(bt); bt.setText(String.valueOf(8)); that.value = 8; snoop(); bd.dismiss();
        }});
        bt9.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            colourButton(bt); bt.setText(String.valueOf(9)); that.value = 9; snoop(); bd.dismiss();
        }});
        bt0.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            colourButton(bt); bt.setText(""); that.value = 0; snoop(); bd.dismiss();
        }});
        btX.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            colourButton(bt); snoop(); bd.dismiss();
        }});
        btH.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            colourButton(bt);
            if(hint == 0) { bd.dismiss(); return; } hint--;
            bt.setText(String.valueOf(that.valueCorr)); that.value = that.valueCorr; that.fixed = true; snoop(); bd.dismiss();
        }});

    }

    // Metoda bojanja običnog gumba
    void colourButton(Button bt) {
        ShapeDrawable shapedrawable = new ShapeDrawable();
        shapedrawable.setShape(new RectShape());
        shapedrawable.getPaint().setColor(Color.WHITE);
        shapedrawable.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        bt.setBackground(shapedrawable);
    }

    // Metoda za ispitivanje je li riješen sudoku
    boolean solved() {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                if(table[i][j].value == 0) return false;
            }
        }
        return true;
    }

    // Podmetoda slijedeče metode koja ispituje ispravnost ploče za fragmente: kocke, retke ili stupce od 9 vrijednosti po pravilima sudokua
    boolean correct(int i1, int j1, int i2, int j2) {
        boolean[] seen = new boolean[10];
        for(int i = 0; i < 9; i++) {
            seen[i] = false;
        }
        for(int i = i1; i < i2; i++) {
            for(int j = j1; j < j2; j++) {
                int value = table[i][j].value;
                if(value != 0){
                    if(seen[value]) return false;
                    seen[value] = true;
                }
            }
        }
        return true;
    }

    // Metoda za ispitivanje je li trenutno stanje sudoku ploče ispravno
    boolean correct() {
        for(int i = 0; i < 9; i++) {
            if(!correct(i, 0, i + 1, 9)) return false;
        }
        for(int j = 0; j < 9; j++) {
            if(!correct(0, j, 9, j + 1)) return false;
        }
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(!correct(3 * i, 3 * j, 3 * i + 3, 3 * j + 3)) return false;
            }
        }
        return true;
    }

    // Vrijednosti vezane uz pohranu gumba sudokua, čistih vrijednosti i komponiranja samog igrivog dijela u prikaz
    Cell[][] table;

    TableLayout tl;
    TextView tv;
    Button btNext, btSurr;
    LinearLayout ll;

    static String[][] field = new String[9][9];
    static String[][] solution = new String[9][9];

    // Metoda za stvaranje popunjene sudoku ploče
    public static void generate() {
        int k, n = 1;
        for(int i = 0; i < 9; i++) {
            k = n;
            for(int j = 0; j < 9; j++) {
                if(k <= 9){
                    field[i][j] = String.valueOf(k);
                    k++;
                } else{
                    k = 1;
                    field[i][j] = String.valueOf(k);
                    k++;
                }
            }
            n = k + 3;
            if(k == 10) n = 4;
            if(n > 9) n = (n % 9) + 1;
        }
    }

    // Metoda koja sa određenom slučajnosti poziva slijedeće metode miješanja vrijednosti popunjenog sudokua
    public static void random_gen(int check) {
        int k1, k2, max = 2, min = 0;
        Random r = new Random();
        for(int i = 0; i < 3; i++) {
            k1 = r.nextInt(max - min + 1) + min;
            do{
                k2 = r.nextInt(max - min + 1) + min;
            } while(k1 == k2);
            max += 3;min += 3;
            if(check == 1) permutation_row(k1, k2);
            else if(check == 0) permutation_col(k1, k2);
        }
    }

    // Metoda kojom premiještamo cijele triseve redaka bez narušavanja ispravnosti sudokua
    public static void permutation_row(int k1, int k2) {
        String temp;
        for(int j = 0; j < 9; j++) {
            temp = field[k1][j];
            field[k1][j] = field[k2][j];
            field[k2][j] = temp;
        }
    }

    // Metoda kojom premiještamo cijele triseve stupaca bez narušavanja ispravnosti sudokua
    public static void permutation_col(int k1, int k2) {
        String temp;
        for(int j = 0; j < 9; j++) {
            temp = field[j][k1];
            field[j][k1] = field[j][k2];
            field[j][k2] = temp;
        }
    }

    // Metoda koja unutar triseva redaka premiješta retke bez narušavanja ispravnosti sudokua
    public static void row_change(int k1, int k2) {
        String temp;
        for(int n = 1; n <= 3; n++) {
            for(int i = 0; i < 9; i++) {
                temp = field[k1][i];
                field[k1][i] = field[k2][i];
                field[k2][i] = temp;
            }
            k1++;
            k2++;
        }
    }

    // Metoda koja unutar triseva redaka premiješta retke bez narušavanja ispravnosti sudokua
    public static void col_change(int k1, int k2) {
        String temp;
        for(int n = 1; n <= 3; n++) {
            for(int i = 0; i < 9; i++) {
                temp = field[i][k1];
                field[i][k1] = field[i][k2];
                field[i][k2] = temp;
            }
            k1++;
            k2++;
        }
    }

    // Metoda za izbijanje vrijednosti popunjene sudoku ploče kako bi ju igrač mogao riješiti
    public static void strike_out(int k1, int k2) {
        int row_from, row_to, col_from, col_to, rem1, rem2, flag;
        String temp = field[k1][k2];
        int count = 9;
        for(int i = 1; i <= 9; i++) {
            flag = 1;
            for(int j = 0; j < 9; j++) {
                if(j != k2) {
                    if(i != Integer.parseInt(field[k1][j])) continue;
                    else {
                        flag = 0;
                        break;
                    }
                }
            }
            if(flag == 1) {
                for(int c = 0;c<9;c++) {
                    if(c!=k1) {
                        if(i!=Integer.parseInt(field[c][k2])) continue;
                        else {
                            flag = 0;
                            break;
                        }
                    }
                }
            }
            if(flag == 1) {
                rem1 = k1 % 3; rem2 = k2 % 3;
                row_from = k1 - rem1; row_to = k1 + (2 - rem1);
                col_from = k2 - rem2; col_to = k2 + (2 - rem2);
                for(int c = row_from; c <= row_to; c++) {
                    for(int b = col_from; b <= col_to; b++) {
                        if(c != k1 && b != k2) {
                            if(i != Integer.parseInt(field[c][b])) continue;
                            else {
                                flag = 0;
                                break;
                            }
                        }
                    }
                }
            }
            if(flag == 0) count--;
        }
        if(count == 1) field[k1][k2]="0";
    }

    // Vrijednost broja pomoči pri popunjavanju
    int hint;

    // Stvori novu ploču pomoću već napisanih metoda popunjavanja, izmjene i izbacivanja vrijednosti
    void createNewTable() {
        hint = 4 - diffHint;

        int counter = 1, k1, k2;
        generate();
        random_gen(1);
        random_gen(0);

        Random rand = new Random();
        int n[] = {0, 3, 6};
        for(int i = 0; i < 2; i++) {
            k1 = n[rand.nextInt(n.length)];
            do{
                k2 = n[rand.nextInt(n.length)];
            } while(k1 == k2);
            if(counter == 1) row_change(k1, k2);
            else col_change(k1, k2);
            counter++;
        }

        // Spremi solution tablicu, odvoji rezultatnu od one za samu igru
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                solution[i][j] = field[i][j];
            }
        }

        Random rand1 = new Random();

        for(k1 = 0; k1 < 9; k1 = k1 + (rand1.nextInt(2)) + 1) {
            for(k2 = 0; k2 < 9; k2 = k2 + (rand1.nextInt(2)) + 1)
                strike_out(k1, k2);
        }
        createTable(false);
    }

    // Pripremljenu ploču materijaliziraj na ekranu, nevezano je li stvorena sada, prije, ili uslijed predaje igre
    void createTable(boolean surrendered){
        tv = new TextView(this);

        String[] split;
        if(!surrendered) {
            tv.setText("New sudoku generated!!!");

            if(!muted) {
                // Muzika pokretanja ploče za igru
                MediaPlayer song = MediaPlayer.create(MainActivity.this, R.raw.short_success);
                song.start();
            }

            tv.setTextColor(Color.BLUE);
            String input = "";
            for(int i = 0; i < 9; i++) {
                for(int j = 0; j < 9; j++) {
                    input = input + field[i][j] + " ";
                }
            }
            split = input.split(" ");
        }
        else {
            tv.setText("You have surrendered the sudoku!!!");

            // Muzika predaje igre
            if(!muted) {
                MediaPlayer song = MediaPlayer.create(MainActivity.this, R.raw.failure_drums);
                song.start();
            }

            tv.setTextColor(Color.RED);
            String input = "";
            for(int i = 0; i < 9; i++) {
                for(int j = 0; j < 9; j++) {
                    input = input + solution[i][j] + " ";
                }
            }
            split = input.split(" ");
        }

        table = new Cell[9][9];

        tl = new TableLayout(this);

        tl.setBackgroundColor(Color.BLACK);

        TableLayout.LayoutParams lp1 = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        lp1.setMargins(10,10,10,10);
        tl.setLayoutParams(lp1);

        for(int i = 0; i < 9; i++) {
            TableRow tr = new TableRow(this);
            if(i == 2 || i == 5) {
                TableLayout.LayoutParams lp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(2,2,2,10);
                tr.setLayoutParams(lp);
            }
            else {
                TableLayout.LayoutParams lp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(2,2,2,2);
                tr.setLayoutParams(lp);
            }

            for(int j = 0; j < 9; j++) {
                String s = split[i*9+j];
                Character c = s.charAt(0);

                if(surrendered == true) {
                    if(field[i][j] == "0") {
                        table[i][j] = new Cell(c == '0' ? 0 : c - '0', true, this);
                    }
                    else {
                        table[i][j] = new Cell(c == '0' ? 0 : c - '0', false, this);
                    }
                }
                else {
                    table[i][j] = new Cell(c == '0' ? 0 : c - '0', false, this);
                }
                table[i][j].setValueCorr(Integer.valueOf(solution[i][j]));
                if(j == 2 || j == 5) {
                    android.widget.TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(2, 2, 10, 2);
                    tr.addView(table[i][j].bt, layoutParams);
                } else {
                    android.widget.TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(2, 2, 2, 2);
                    tr.addView(table[i][j].bt, layoutParams);
                }
            }
            tl.addView(tr);
        }
        tl.setShrinkAllColumns(true);

        ll = new LinearLayout(this);

        btNext = new Button(this);
        btNext.setText("Next");
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { createNewTable(); }
        });

        btSurr = new Button(this);
        btSurr.setText("Surrender");
        btSurr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { createTable(true); }
        });

        ll.addView(tl);
        ll.addView(tv);
        ll.addView(btSurr);
        ll.addView(btNext);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);
        setContentView(ll);
    }

    // Main Menu layout
    LinearLayout ml;

    // Metoda koja kreira i prikazuje Main Menu aplikacije
    void menu() {
        menu = true;
        settings = false;

        ml = new LinearLayout(this);
        ml.setOrientation(LinearLayout.VERTICAL);

        Button play, exit, cont, sett;
        TextView title, meta;
        title = new TextView(this);
        play = new Button(this);
        exit = new Button(this);
        cont = new Button(this);
        sett = new Button(this);
        title.setText("MAIN MENU");
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
        play.setText("NEW GAME");
        exit.setText("EXIT");
        cont.setText("CONTINUE");
        sett.setText("SETTINGS");
        ml.addView(title);
        ml.addView(cont);
        ml.addView(play);
        ml.addView(sett);
        ml.addView(exit);

        ml.setGravity(Gravity.CENTER_HORIZONTAL);
        ml.setGravity(Gravity.CENTER_VERTICAL);

        meta = new TextView(this);
        meta.setText("Sudoku+ v1.0: Made by Marko Gabrovec");
        meta.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
        meta.setGravity(Gravity.CENTER_HORIZONTAL);

        ml.addView(meta);

        if(table == null) {
            cont.setEnabled(false);
        } else {
            cont.setEnabled(true);
        }

        cont.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            setContentView(ll); menu = false;
        }});
        play.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            createNewTable(); menu = false;
        }});
        sett.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            menu = true; settings();
        }});
        exit.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            finish();
        }});

        setContentView(ml);
    }

    // Boolean vrijednost koja pokazuje, je li igrač ugasio zvuk igre
    boolean muted = false;

    // Upali/ugasi zvuk i promijeni tekst gumba za zvuk ovisno o stanju
    void soundSwap() {
        if(muted == false) muted = true;
        else muted = false;

        String buttSound = "SOUND:  ";
        if(muted == true) buttSound = buttSound + "OFF";
        else buttSound = buttSound + "ON";
        sound.setText(buttSound);
    }

    // Vrijednost za definiranje broja hintova prema težini
    int diffHint = 2;

    // Promjena težine kroz broj hintova i promjena teksta gumba za težinu
    void diffSwap() {
        if(diffHint == 4) diffHint = 0;
        else diffHint++;
        String buttDiff = "HINTS:  ";
        if(diffHint == 4) buttDiff = buttDiff + "VERY HARD(0)";
        else if(diffHint == 3) buttDiff = buttDiff + "HARD(1)";
        else if(diffHint == 2) buttDiff = buttDiff + "NORMAL(2)";
        else if(diffHint == 1) buttDiff = buttDiff + "EASY(3)";
        else if(diffHint == 0) buttDiff = buttDiff + "VERY EASY(4)";
        diff.setText(buttDiff);
    }

    //Settings Menu layout
    LinearLayout sl;
    Button sound;
    Button diff;

    // Metoda koja kreira i prikazuje Settings Menu aplikacije
    void settings() {
        settings = true;

        sl = new LinearLayout(this);
        sl.setOrientation(LinearLayout.VERTICAL);

        Button exit;
        TextView title;
        title = new TextView(this);
        diff = new Button(this);
        exit = new Button(this);
        sound = new Button(this);
        title.setText("SETTINGS MENU");
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);

        String buttDiff = "HINTS:  ";
        if(diffHint == 4) buttDiff = buttDiff + "VERY HARD(0)";
        else if(diffHint == 3) buttDiff = buttDiff + "HARD(1)";
        else if(diffHint == 2) buttDiff = buttDiff + "NORMAL(2)";
        else if(diffHint == 1) buttDiff = buttDiff + "EASY(3)";
        else if(diffHint == 0) buttDiff = buttDiff + "VERY EASY(4)";
        diff.setText(buttDiff);

        exit.setText("BACK");
        String buttSound = "SOUND:  ";
        if(muted == true) buttSound = buttSound + "OFF";
        else buttSound = buttSound + "ON";
        sound.setText(buttSound);

        sl.addView(title);
        sl.addView(diff);
        sl.addView(sound);
        sl.addView(exit);
        sl.setGravity(Gravity.CENTER_HORIZONTAL);
        sl.setGravity(Gravity.CENTER_VERTICAL);

        diff.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            diffSwap();
        }});
        sound.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            soundSwap();
        }});
        exit.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            settings = false;
            setContentView(ml);
        }});

        setContentView(sl);
    }

    // Stvaranje aplikacije
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        menu();
    }

    // Vrijednost nalazimo se u meniju i ako da nalazimo li se u Settings dijelu menija
    boolean menu;
    boolean settings;

    // Metoda funkcionalnosti gumba u traci aplikacije
    @Override
    public boolean onSupportNavigateUp() {
        // Ako si u meniju
        if(menu == true) {
            // A nisi u Settings, izađi, ali pusti igru u pozadini
            if(settings == false) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
            // A jesi u Settings, izađi iz Settings
            else {
                settings = false;
                menu();
            }
        }
        // Ako nisi u meniju idi u menij
        else {
            menu = true;
            menu();
        }
        return true;
    }

    // Metoda funkcionalnosti gumba povratka samog uređaja
    @Override
    public void onBackPressed() {
        // Ako si u meniju
        if(menu == true) {
            // A nisi u Settings, izađi, ali pusti igru u pozadini
            if(settings == false) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
            // A jesi u Settings, izađi iz Settings
            else {
                settings = false;
                menu();
            }
        }
        // Ako nisi u meniju idi u menij
        else {
            menu = true;
            menu();
        }
    }
}