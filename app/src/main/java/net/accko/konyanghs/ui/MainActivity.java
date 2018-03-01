package net.accko.konyanghs.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.accko.konyanghs.R;
import net.accko.konyanghs.bap.BapTool;
import net.accko.konyanghs.bap.ProcessTask;
import net.accko.konyanghs.notice.BBSListAdapter;
import net.accko.konyanghs.notice.ListData;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView bapText, notiText, sunText, monText, tueText, wedText, thuText, friText, satText, morText, lunText, dinText, showBap;
    CardView bapLayout;
    View timeShadow;
    LinearLayout timeLayout;
    RelativeLayout helloLayout;
    ImageButton settings_btn, contact_btn;
    Animation fadeinbaplayout, fadeintimelayout, fadeoutbaplayout, fadeouttimelayout;

    String sunmor, sunlun, sundin, monmor, monlun, mondin, tuemor, tuelun, tuedin, wedmor, wedlun, weddin, thumor, thulun, thudin, frimor, frilun, fridin, satmor, satlun, satdin;
    BapDownloadTask mProcessTask;
    AlertDialog bapProgress, notiProgress;
    ProgressBar progressBar;
    Calendar isCalender, isBapCalender;
    boolean isUpdating = false; // 급식 파서
    boolean isnotied = false;

    int isToday, isTime, isClickedDay, isClickedMenu;

    private static String URL_PRIMARY = "http://www.konyang.cnehs.kr/"; //홈페이지 원본 주소이다.
    private static String GETNOTICE = "boardCnts/list.do?boardID=212540&m=1101&s=konyang#contents"; //홈페이지 의 게시판을 나타내는 뒤 주소, 비슷한 게시판들은 거의 파싱이 가능하므로 응용하여 사용하자.
    private String url;
    private URL URL;

    private Source source;
    private BBSListAdapter BBSAdapter = null;
    private ListView BBSList;
    private int BD_table;

    private ConnectivityManager cManager;
    private NetworkInfo mobile;
    private NetworkInfo wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defaultView(); //뷰 기본 설정
        getBapList(true);

        isToday = isBapCalender.get(Calendar.DAY_OF_WEEK);
        isTime = isBapCalender.get(Calendar.HOUR_OF_DAY);

        BBSList = findViewById(R.id.noticelist); //리스트선언
        BBSList.setVisibility(View.INVISIBLE);
        BBSAdapter = new BBSListAdapter(this);
        BBSList.setAdapter(BBSAdapter); //리스트에 어댑터를 먹여준다.
        BBSList.setOnItemClickListener( //리스트 클릭시 실행될 로직 선언
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        ListData mData = BBSAdapter.mListData.get(position); // 클릭한 포지션의 데이터를 가져온다.
                        String URL_BCS = mData.mUrl; // 가져온 데이터 중 url 부분만 적출해낸다.
                        // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_PRIMARY + "boardCnts/view.do?boardID=212540&viewBoardID=212540&boardSeq=" + URL_BCS + "&lev=0&action=view&searchType=null&statusYN=W&page=1"))); //클릭된 링크로 바로 연결 오류남
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.konyang.cnehs.kr/boardCnts/list.do?boardID=212540&m=1101&s=konyang#contents")));
                    }
                });
        url = URL_PRIMARY + GETNOTICE; // 파싱하기전 PRIMARY URL 과 공지사항 URL 을 합쳐 완전한 URL을 만든다.
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bapText:
                helloLayout.setVisibility(View.GONE);

                if (isClickedMenu == 0) {
                    bapLayout.setVisibility(View.VISIBLE);
                    bapLayout.startAnimation(fadeinbaplayout);
                    timeLayout.setVisibility(View.VISIBLE);
                    timeLayout.startAnimation(fadeintimelayout);

                    showBap.setVisibility(View.VISIBLE);

                    if (isToday == 1) {
                        sattimereflected();
                    } else if (isToday == 2) {
                        montimereflected();
                    } else if (isToday == 3) {
                        tuetimereflected();
                    } else if (isToday == 4) {
                        wedtimereflected();
                    } else if (isToday == 5) {
                        thutimereflected();
                    } else if (isToday == 6) {
                        fritimereflected();
                    } else if (isToday == 7) {
                        sattimereflected();
                    }
                }

                if (isClickedMenu == 1) {
                }

                if (isClickedMenu == 2) {
                    BBSList.setVisibility(View.INVISIBLE);
                    BBSList.startAnimation(fadeoutbaplayout);

                    bapLayout.setVisibility(View.VISIBLE);
                    bapLayout.startAnimation(fadeinbaplayout);
                    timeLayout.setVisibility(View.VISIBLE);
                    timeLayout.startAnimation(fadeintimelayout);

                    showBap.setVisibility(View.VISIBLE);

                    if (isToday == 1) {
                        sattimereflected();
                    } else if (isToday == 2) {
                        montimereflected();
                    } else if (isToday == 3) {
                        tuetimereflected();
                    } else if (isToday == 4) {
                        wedtimereflected();
                    } else if (isToday == 5) {
                        thutimereflected();
                    } else if (isToday == 6) {
                        fritimereflected();
                    } else if (isToday == 7) {
                        sattimereflected();
                    }
                }

                menuAlpha(255, 50);
                isClickedMenu = 1; //급식을 누른 상태
                break;

            case R.id.notiText:
                helloLayout.setVisibility(View.GONE);

                if (isClickedMenu == 0) {
                    BBSList.setVisibility(View.VISIBLE);
                    BBSList.startAnimation(fadeintimelayout);

                    if (isnotied == false) {
                        if (isInternetCon()) { //false 반환시 if 문안의 로직 실행
                            Toast.makeText(MainActivity.this, getString(R.string.notconnect_internet), Toast.LENGTH_SHORT).show();
                        } else { //인터넷 체크 통과시 실행할 로직
                            try {
                                process(); //네트워크 관련은 따로 쓰레드를 생성해야 UI 쓰레드와 겹치지 않는다. 그러므로 Thread 가 선언된 process 메서드를 호출한다.
                                BBSAdapter.notifyDataSetChanged();
                                isnotied = true;
                            } catch (Exception e) {
                                Log.d("ERROR", e + "");
                            }
                        }
                    }
                }

                if (isClickedMenu == 1) {
                    BBSList.setVisibility(View.VISIBLE);
                    BBSList.startAnimation(fadeinbaplayout);

                    bapLayout.setVisibility(View.INVISIBLE);
                    bapLayout.startAnimation(fadeoutbaplayout);
                    timeLayout.setVisibility(View.INVISIBLE);
                    timeLayout.startAnimation(fadeoutbaplayout);
                    showBap.setVisibility(View.INVISIBLE);
                    showBap.startAnimation(fadeoutbaplayout);

                    if (isnotied == false) {
                        if (isInternetCon()) { //false 반환시 if 문안의 로직 실행
                            Toast.makeText(MainActivity.this, getString(R.string.notconnect_internet), Toast.LENGTH_SHORT).show();
                        } else { //인터넷 체크 통과시 실행할 로직
                            try {
                                process(); //네트워크 관련은 따로 쓰레드를 생성해야 UI 쓰레드와 겹치지 않는다. 그러므로 Thread 가 선언된 process 메서드를 호출한다.
                                BBSAdapter.notifyDataSetChanged();
                                isnotied = true;
                            } catch (Exception e) {
                                Log.d("ERROR", e + "");
                            }
                        }
                    }
                }

                if (isClickedMenu == 2) {
                }

                menuAlpha(50, 255);
                isClickedMenu = 2; //공지사항을 누른 상태
                break;

            case R.id.sunText:
                suntimereflected();
                break;

            case R.id.monText:
                montimereflected();
                break;

            case R.id.tueText:
                tuetimereflected();
                break;

            case R.id.wedText:
                wedtimereflected();
                break;

            case R.id.thuText:
                thutimereflected();
                break;

            case R.id.friText:
                fritimereflected();
                break;

            case R.id.satText:
                sattimereflected();
                break;

            case R.id.morText: // 아침
                timeAlpha(255, 50, 50);
                if (isClickedDay == 0) {
                    showBap.setText(sunmor);
                }
                if (isClickedDay == 1) {
                    showBap.setText(monmor);
                }
                if (isClickedDay == 2) {
                    showBap.setText(tuemor);
                }
                if (isClickedDay == 3) {
                    showBap.setText(wedmor);
                }
                if (isClickedDay == 4) {
                    showBap.setText(thumor);
                }
                if (isClickedDay == 5) {
                    showBap.setText(frimor);
                }
                if (isClickedDay == 6) {
                    showBap.setText(satmor);
                }
                break;

            case R.id.lunText: // 점심
                timeAlpha(50, 255, 50);
                if (isClickedDay == 0) {
                    showBap.setText(sunlun);
                }
                if (isClickedDay == 1) {
                    showBap.setText(monlun);
                }
                if (isClickedDay == 2) {
                    showBap.setText(tuelun);
                }
                if (isClickedDay == 3) {
                    showBap.setText(wedlun);
                }
                if (isClickedDay == 4) {
                    showBap.setText(tuelun);
                }
                if (isClickedDay == 5) {
                    showBap.setText(frilun);
                }
                if (isClickedDay == 6) {
                    showBap.setText(satlun);
                }
                break;

            case R.id.dinText: // 저녁
                timeAlpha(50, 50, 255);
                if (isClickedDay == 0) {
                    showBap.setText(sundin);
                }
                if (isClickedDay == 1) {
                    showBap.setText(mondin);
                }
                if (isClickedDay == 2) {
                    showBap.setText(tuedin);
                }
                if (isClickedDay == 3) {
                    showBap.setText(weddin);
                }
                if (isClickedDay == 4) {
                    showBap.setText(tuedin);
                }
                if (isClickedDay == 5) {
                    showBap.setText(fridin);
                }
                if (isClickedDay == 6) {
                    showBap.setText(satdin);
                }
                break;

            case R.id.settings_btn:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.contact_btn:
                Intent contact = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:me@accko.net"));
                startActivity(contact);
                break;
        }
    }

    private void suntimereflected() { // 시간에 따른 급식 보기 설정 일요일
        bapAlpha(255, 50, 50, 50, 50, 50, 50);
        if (0 <= isTime && isTime <= 8) {
            timeAlpha(255, 50, 50);
            showBap.setText(sunmor);
        }
        if (8 < isTime && isTime <= 13) {
            timeAlpha(50, 255, 50);
            showBap.setText(sunlun);
        }
        if (13 < isTime && isTime <= 24) {
            timeAlpha(50, 50, 255);
            showBap.setText(sundin);
        }
        isClickedDay = 0;
    }

    private void montimereflected() { // 시간에 따른 급식 보기 설정 월요일
        bapAlpha(50, 255, 50, 50, 50, 50, 50);
        if (0 <= isTime && isTime <= 8) {
            timeAlpha(255, 50, 50);
            showBap.setText(monmor);
        }
        if (8 < isTime && isTime <= 13) {
            timeAlpha(50, 255, 50);
            showBap.setText(monlun);
        }
        if (13 < isTime && isTime <= 24) {
            timeAlpha(50, 50, 255);
            showBap.setText(mondin);
        }
        isClickedDay = 1;
    }

    private void tuetimereflected() { // 시간에 따른 급식 보기 설정 화요일
        bapAlpha(50, 50, 255, 50, 50, 50, 50);
        if (0 <= isTime && isTime <= 8) {
            timeAlpha(255, 50, 50);
            showBap.setText(tuemor);
        }
        if (8 < isTime && isTime <= 13) {
            timeAlpha(50, 255, 50);
            showBap.setText(tuelun);
        }
        if (13 < isTime && isTime <= 24) {
            timeAlpha(50, 50, 255);
            showBap.setText(tuelun);
        }
        isClickedDay = 2;
    }

    private void wedtimereflected() { // 시간에 따른 급식 보기 설정 수요일
        bapAlpha(50, 50, 50, 255, 50, 50, 50);
        if (0 <= isTime && isTime <= 8) {
            timeAlpha(255, 50, 50);
            showBap.setText(wedmor);
        }
        if (8 < isTime && isTime <= 13) {
            timeAlpha(50, 255, 50);
            showBap.setText(wedlun);
        }
        if (13 < isTime && isTime <= 24) {
            timeAlpha(50, 50, 255);
            showBap.setText(weddin);
        }
        isClickedDay = 3;
    }

    private void thutimereflected() { // 시간에 따른 급식 보기 설정 목요일
        bapAlpha(50, 50, 50, 50, 255, 50, 50);
        if (0 <= isTime && isTime <= 8) {
            timeAlpha(255, 50, 50);
            showBap.setText(thumor);
        }
        if (8 < isTime && isTime <= 13) {
            timeAlpha(50, 255, 50);
            showBap.setText(thulun);
        }
        if (13 < isTime && isTime <= 24) {
            timeAlpha(50, 50, 255);
            showBap.setText(thudin);
        }
        isClickedDay = 4;
    }

    private void fritimereflected() { // 시간에 따른 급식 보기 설정 금요일
        bapAlpha(50, 50, 50, 50, 50, 255, 50);
        if (0 <= isTime && isTime <= 8) {
            timeAlpha(255, 50, 50);
            showBap.setText(frimor);
        }
        if (8 < isTime && isTime <= 13) {
            timeAlpha(50, 255, 50);
            showBap.setText(frilun);
        }
        if (13 < isTime && isTime <= 24) {
            timeAlpha(50, 50, 255);
            showBap.setText(fridin);
        }
        isClickedDay = 5;
    }

    private void sattimereflected() { // 시간에 따른 급식 보기 설정 토요일
        bapAlpha(50, 50, 50, 50, 50, 50, 255);
        if (0 <= isTime && isTime <= 8) {
            timeAlpha(255, 50, 50);
            showBap.setText(satmor);
        }
        if (8 < isTime && isTime <= 13) {
            timeAlpha(50, 255, 50);
            showBap.setText(satlun);
        }
        if (13 < isTime && isTime <= 24) {
            timeAlpha(50, 50, 255);
            showBap.setText(satdin);
        }
        isClickedDay = 6;
    }

    private void defaultView() {
        bapText = findViewById(R.id.bapText); // 밥 메뉴 title
        bapText.setOnClickListener(this);
        notiText = findViewById(R.id.notiText); // 공지 메뉴 title
        notiText.setOnClickListener(this);
        menuAlpha(50, 50);

        bapLayout = findViewById(R.id.bapLayout);
        bapLayout.setOnClickListener(this);
        bapLayout.setVisibility(View.INVISIBLE); // 일~토 선택 view

        timeShadow = findViewById(R.id.timeShadow);
        timeShadow.setOnClickListener(this);
        timeShadow.setVisibility(View.INVISIBLE); // 하단 아침 점심 저녁 view 쉐도우 라인

        timeLayout = findViewById(R.id.timeLayout);
        timeLayout.setOnClickListener(this);
        timeLayout.setVisibility(View.INVISIBLE); // 하단 아침 점심 저녁

        helloLayout = findViewById(R.id.helloLayout);

        sunText = findViewById(R.id.sunText);
        sunText.setOnClickListener(this);
        monText = findViewById(R.id.monText);
        monText.setOnClickListener(this);
        tueText = findViewById(R.id.tueText);
        tueText.setOnClickListener(this);
        wedText = findViewById(R.id.wedText);
        wedText.setOnClickListener(this);
        thuText = findViewById(R.id.thuText);
        thuText.setOnClickListener(this);
        friText = findViewById(R.id.friText);
        friText.setOnClickListener(this);
        satText = findViewById(R.id.satText);
        satText.setOnClickListener(this); // 일~토 선택 text
        bapAlpha(50, 50, 50, 50, 50, 50, 50);

        morText = findViewById(R.id.morText);
        morText.setOnClickListener(this);
        lunText = findViewById(R.id.lunText);
        lunText.setOnClickListener(this);
        dinText = findViewById(R.id.dinText);
        dinText.setOnClickListener(this); // 아침 점시 저녁 선택 Text
        timeAlpha(50, 50, 50);

        showBap = findViewById(R.id.showBap);
        showBap.setVisibility(View.INVISIBLE); // 밥 표시 View

        isCalender = Calendar.getInstance();
        isBapCalender = Calendar.getInstance(); // 현재 날짜 설정

        settings_btn = findViewById(R.id.settings_btn);
        settings_btn.setOnClickListener(this);
        contact_btn = findViewById(R.id.contact_btn);
        contact_btn.setOnClickListener(this);

        fadeinbaplayout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein_baplayout);
        fadeintimelayout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein_timelayout);
        fadeoutbaplayout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout_baplayout);
        fadeouttimelayout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout_timelayout); // 애니메이션 설정

        isClickedMenu = 0; // 메뉴를 선택하지 않은 상태
    } // 뷰 기본 설정

    private void menuAlpha(int bapAlpha, int notiAlpha) {
        bapText.setTextColor(Color.argb(bapAlpha, Color.red(48), Color.green(48), Color.blue(48)));
        notiText.setTextColor(Color.argb(notiAlpha, Color.red(48), Color.green(48), Color.blue(48)));
    } // 메뉴 투명도 설정

    private void bapAlpha(int sunAlpha, int monAlpha, int tueAlpha, int wedAlpha, int thuAlpha, int friAlpha, int satAlpha) {
        sunText.setTextColor(Color.argb(sunAlpha, Color.red(48), Color.green(48), Color.blue(48)));
        monText.setTextColor(Color.argb(monAlpha, Color.red(48), Color.green(48), Color.blue(48)));
        tueText.setTextColor(Color.argb(tueAlpha, Color.red(48), Color.green(48), Color.blue(48)));
        wedText.setTextColor(Color.argb(wedAlpha, Color.red(48), Color.green(48), Color.blue(48)));
        thuText.setTextColor(Color.argb(thuAlpha, Color.red(48), Color.green(48), Color.blue(48)));
        friText.setTextColor(Color.argb(friAlpha, Color.red(48), Color.green(48), Color.blue(48)));
        satText.setTextColor(Color.argb(satAlpha, Color.red(48), Color.green(48), Color.blue(48)));
    } // 일~토 투명도 설정

    private void timeAlpha(int morAlpha, int lunAlpha, int dinAlpha) {
        morText.setTextColor(Color.argb(morAlpha, Color.red(48), Color.green(48), Color.blue(48)));
        lunText.setTextColor(Color.argb(lunAlpha, Color.red(48), Color.green(48), Color.blue(48)));
        dinText.setTextColor(Color.argb(dinAlpha, Color.red(48), Color.green(48), Color.blue(48)));
    } // 아침 점심 저녁 투명도 설정

    public class BapDownloadTask extends ProcessTask {
        public BapDownloadTask(Context mContext) {
            super(mContext);
        }

        @Override
        public void onPreDownload() {
            isUpdating = true;
        }

        @Override
        public void onUpdate(int progress) {
            progressBar.setProgress(progress);
        }

        @Override
        public void onFinish(long result) {
            if (bapProgress != null) {
                bapProgress.dismiss();
            }

            isUpdating = false;
            getBapList(false);
        }
    }

    private void getBapList(boolean isUpdate) {
        if (isCalender == null) {
            isCalender = Calendar.getInstance();
        } // isCalender이 null이면 새로 instance

        isCalender.add(Calendar.DATE, 1 - isCalender.get(Calendar.DAY_OF_WEEK)); //일요일을 첫 번째로 설정

        for (int i = 0; i < 7; i++) {
            int year = isCalender.get(Calendar.YEAR);
            int month = isCalender.get(Calendar.MONTH);
            int day = isCalender.get(Calendar.DAY_OF_MONTH);

            BapTool.restoreBapDateClass savedBapData =
                    BapTool.restoreBapData(getApplicationContext(), year, month, day);

            if (savedBapData.isBlankDay) {
                if (!isUpdating && isUpdate) {
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.dialog_progress, null);
                    TextView progressText = dialogView.findViewById(R.id.progressText);
                    progressText.setText(getString(R.string.baproading));
                    progressBar = dialogView.findViewById(R.id.progressBar);
                    progressBar.setMax(100);
                    AlertDialog.Builder bapDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    bapDialogBuilder.setView(dialogView);
                    bapProgress = bapDialogBuilder.create();
                    bapProgress.show();
                    bapProgress.setCancelable(false);

                    mProcessTask = new BapDownloadTask(getApplicationContext());
                    mProcessTask.execute(year, month, day);
                }
                return;
            }

            if (i == 0) { // 일요일
                sunmor = setCharReplace(savedBapData.Morning);
                if (BapTool.mStringCheck(savedBapData.Morning)) {
                    sunmor = getString(R.string.bap_error);
                }
                sunlun = setCharReplace(savedBapData.Lunch);
                if (BapTool.mStringCheck(savedBapData.Lunch)) {
                    sunlun = getString(R.string.bap_error);
                }
                sundin = setCharReplace(savedBapData.Dinner);
                if (BapTool.mStringCheck(savedBapData.Dinner)) {
                    sundin = getString(R.string.bap_error);
                }
            }

            if (i == 1) { // 월요일
                monmor = setCharReplace(savedBapData.Morning);
                if (BapTool.mStringCheck(savedBapData.Morning)) {
                    monmor = getString(R.string.bap_error);
                }
                monlun = setCharReplace(savedBapData.Lunch);
                if (BapTool.mStringCheck(savedBapData.Lunch)) {
                    monlun = getString(R.string.bap_error);
                }
                mondin = setCharReplace(savedBapData.Dinner);
                if (BapTool.mStringCheck(savedBapData.Dinner)) {
                    mondin = getString(R.string.bap_error);
                }
            }

            if (i == 2) { // 화요일
                tuemor = setCharReplace(savedBapData.Morning);
                if (BapTool.mStringCheck(savedBapData.Morning)) {
                    tuemor = getString(R.string.bap_error);
                }
                tuelun = setCharReplace(savedBapData.Lunch);
                if (BapTool.mStringCheck(savedBapData.Lunch)) {
                    tuelun = getString(R.string.bap_error);
                }
                tuedin = setCharReplace(savedBapData.Dinner);
                if (BapTool.mStringCheck(savedBapData.Dinner)) {
                    tuedin = getString(R.string.bap_error);
                }
            }

            if (i == 3) { // 수요일
                wedmor = setCharReplace(savedBapData.Morning);
                if (BapTool.mStringCheck(savedBapData.Morning)) {
                    wedmor = getString(R.string.bap_error);
                }
                wedlun = setCharReplace(savedBapData.Lunch);
                if (BapTool.mStringCheck(savedBapData.Lunch)) {
                    wedlun = getString(R.string.bap_error);
                }
                weddin = setCharReplace(savedBapData.Dinner);
                if (BapTool.mStringCheck(savedBapData.Dinner)) {
                    weddin = getString(R.string.bap_error);
                }
            }

            if (i == 4) { // 목요일
                thumor = setCharReplace(savedBapData.Morning);
                if (BapTool.mStringCheck(savedBapData.Morning)) {
                    thumor = getString(R.string.bap_error);
                }
                thulun = setCharReplace(savedBapData.Lunch);
                if (BapTool.mStringCheck(savedBapData.Lunch)) {
                    thulun = getString(R.string.bap_error);
                }
                thudin = setCharReplace(savedBapData.Dinner);
                if (BapTool.mStringCheck(savedBapData.Dinner)) {
                    thudin = getString(R.string.bap_error);
                }
            }

            if (i == 5) { // 금요일
                frimor = setCharReplace(savedBapData.Morning);
                if (BapTool.mStringCheck(savedBapData.Morning)) {
                    frimor = getString(R.string.bap_error);
                }
                frilun = setCharReplace(savedBapData.Lunch);
                if (BapTool.mStringCheck(savedBapData.Lunch)) {
                    frilun = getString(R.string.bap_error);
                }
                fridin = setCharReplace(savedBapData.Dinner);
                if (BapTool.mStringCheck(savedBapData.Dinner)) {
                    fridin = getString(R.string.bap_error);
                }
            }

            if (i == 6) { // 토요일
                satmor = setCharReplace(savedBapData.Morning);
                if (BapTool.mStringCheck(savedBapData.Morning)) {
                    satmor = getString(R.string.bap_error);
                }
                satlun = setCharReplace(savedBapData.Lunch);
                if (BapTool.mStringCheck(savedBapData.Lunch)) {
                    satlun = getString(R.string.bap_error);
                }
                satdin = setCharReplace(savedBapData.Dinner);
                if (BapTool.mStringCheck(savedBapData.Dinner)) {
                    satdin = getString(R.string.bap_error);
                }
            }

            isCalender.add(Calendar.DATE, 1);
        }
    }

    public static String setCharReplace(String written) {
        String shifted;
        String[] filtered = {"\\.", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        for (int i = 0; i < filtered.length; i++) {
            shifted = written.replaceAll(filtered[i], "");
            written = shifted.trim();
        }

        return written;
    }

    private void process() throws IOException {
        new Thread() {
            @Override
            public void run() {
                Handler Progress = new Handler(Looper.getMainLooper()); //네트워크 쓰레드와 별개로 따로 핸들러를 이용하여 쓰레드를 생성한다.
                Progress.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LayoutInflater inflater = getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.dialog_progress, null);
                        TextView progressText = dialogView.findViewById(R.id.progressText);
                        progressText.setText(getString(R.string.notiroading));
                        AlertDialog.Builder notiDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        notiDialogBuilder.setView(dialogView);
                        notiProgress = notiDialogBuilder.create();
                        notiProgress.show();
                        notiProgress.setCancelable(false);
                    }
                }, 0);

                try {
                    URL = new URL(url);
                    InputStream html = URL.openStream();
                    source = new Source(new InputStreamReader(html, "utf-8")); //소스를 UTF-8 인코딩으로 불러온다.
                    source.fullSequentialParse(); //순차적으로 구문분석
                } catch (Exception e) {
                    Log.d("ERROR", e + "");
                }

                List<StartTag> tabletags = source.getAllStartTags(HTMLElementName.DIV); // DIV 타입의 모든 태그들을 불러온다.

                for (int arrnum = 0; arrnum < tabletags.size(); arrnum++) { //DIV 모든 태그중 BD_table 태그가 몇번째임을 구한다.
                    if (tabletags.get(arrnum).toString().equals("<div class=\"BD_table\">")) {
                        BD_table = arrnum; //DIV 클래스가 bbsContent 면 arrnum 값을 BD_table 로 몇번째인지 저장한다.
                        Log.d("BBSLOCATES", arrnum + ""); //arrnum 로깅
                        break;
                    }
                }

                Element BBS_DIV = source.getAllElements(HTMLElementName.DIV).get(BD_table); //BD_table 번째 의 DIV 를 모두 가져온다.
                Element BBS_TABLE = BBS_DIV.getAllElements(HTMLElementName.TABLE).get(0); //테이블 접속
                Element BBS_TBODY = BBS_TABLE.getAllElements(HTMLElementName.TBODY).get(0); //데이터가 있는 TBODY 에 접속


                for (int C_TR = 0; C_TR < BBS_TBODY.getAllElements(HTMLElementName.TR).size(); C_TR++) { //여기서는 이제부터 게시된 게시물 데이터를 불러와 게시판 인터페이스를 구성할 것이다.
                    // 소스의 효율성을 위해서는 for 문을 사용하는것이 좋지만 , 이해를 돕기위해 소스를 일부로 늘려 두었다.
                    try {
                        Element BBS_TR = BBS_TBODY.getAllElements(HTMLElementName.TR).get(C_TR); //TR 접속

                        Element BC_info = BBS_TR.getAllElements(HTMLElementName.TD).get(1); //URL(onclick) TITLE(title) 을 담은 정보를 불러온다.
                        Element BC_a = BC_info.getAllElements(HTMLElementName.A).get(0); //BC_info 안의 a 태그를 가져온다.
                        String BCS_url = BC_a.getAttributeValue("onclick"); //a 태그의 onclick 는 BCS_url 로 선언
                        String BSC_url_shifted = BCS_url.replace("javascript:goView('212540','212540','", "").replace("', '0', 'null', 'W', '1', 'N', '')", ""); //필요한 중간값을 제외한 모든 문자를 replace해준다.
                        String BCS_title = BC_a.getAttributeValue("title"); //a 태그의 title 은 BCS_title 로 선언

                        Element BC_writer = BBS_TR.getAllElements(HTMLElementName.TD).get(2); //글쓴이를 불러온다.
                        Element BC_date = BBS_TR.getAllElements(HTMLElementName.TD).get(4); // 날짜를 불러온다.

                        String BCS_writer = BC_writer.getContent().toString(); // 작성자값을 담은 엘레먼트의 컨텐츠를 문자열로 변환시켜 가져온다.
                        String BCS_date = BC_date.getContent().toString(); // 작성일자값을 담은 엘레먼트의 컨텐츠를 문자열로 변환시켜 가져온다.

                        BBSAdapter.mListData.add(new ListData(BCS_title, BSC_url_shifted, BCS_writer, BCS_date)); //데이터가 모이면 데이터 리스트 클래스에 데이터들을 등록한다.

                    } catch (Exception e) {
                        Log.d("BCSERROR", e + "");
                    }
                }

                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BBSAdapter.notifyDataSetChanged(); //모든 작업이 끝나면 리스트 갱신
                        notiProgress.dismiss(); //모든 작업이 끝나면 다이어로그 종료
                    }
                }, 0);
            }
        }.start();
    }

    private boolean isInternetCon() {
        cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); //모바일 데이터 여부
        wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); //와이파이 여부
        return !mobile.isConnected() && !wifi.isConnected(); //결과값을 리턴
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (bapProgress != null) {
            bapProgress.dismiss();
        }

        if (notiProgress != null) {
            notiProgress.dismiss(); //다이어로그가 켜져있을경우 (!null) 종료시켜준다
        }

        isCalender = null;
    } // 앱이 중지됐을 때
}