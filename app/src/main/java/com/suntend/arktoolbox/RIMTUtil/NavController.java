package com.suntend.arktoolbox.RIMTUtil;
/*
 * JamXi
 */
import java.util.ArrayList;
import java.util.List;

public class NavController {
    static List<String> navList = new ArrayList<>();
    public static void setNavList(String navlist1,String navlist2,String navlist3,String navlist4,String navlist5){
        navList.add(navlist1);
        navList.add(navlist2);
        navList.add(navlist3);
        navList.add(navlist4);
        navList.add(navlist5);
    }
    public static List<String> getNavList(){
        return navList;
    }
    public static void resetNavList(){
        navList.clear();
    }
}
