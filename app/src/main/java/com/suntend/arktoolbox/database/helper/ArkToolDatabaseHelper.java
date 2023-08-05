package com.suntend.arktoolbox.database.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.suntend.arktoolbox.database.bean.ToolCategoryBean;
import com.suntend.arktoolbox.database.bean.ToolInfoBean;
import com.suntend.arktoolbox.fragment.toolbox.bean.ToolboxBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 明日方舟数据库辅助类
 */
public class ArkToolDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "ArkToolDatabaseHelper";//log标签
    private static final String DB_NAME = "arkToolDB.db";//数据库文件名
    private static final int DB_VERSION = 1;//当前数据库版本号
    public static final int ATDG_ID = 1;//ATDG的分类id，用于显示判断所有固定
    private static ArkToolDatabaseHelper mHelper = null;//单例
    private SQLiteDatabase mDB = null;//数据库对象
    private static final String TABLE_TOOL_CATEGORY = "tool_category";//工具分类表
    private static final String TABLE_TOOL_INFO = "tool_info";//工具详情表
    private static final String TABLE_SEARCH_HISTORY = "search_history";//搜索历史表

    private ArkToolDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 获取实例--单例模式
     *
     * @param context 上下文
     * @return 数据库帮手类
     */
    public static ArkToolDatabaseHelper getInstance(Context context) {
        mHelper = new ArkToolDatabaseHelper(context);
        return mHelper;
    }

    /**
     * 获得数据库连接(如果磁盘满则只能读不能写)
     */
    public SQLiteDatabase openReadLink() {
        if (mDB == null || !mDB.isOpen())
            mDB = mHelper.getReadableDatabase();
        return mDB;
    }

    /**
     * 关闭连接(如果正在进行读写时候关闭会报错，简单调用情况下一般不需要关闭)
     */
    public void closeLink() {
        if (mDB != null && mDB.isOpen()) {
            mDB.close();
            mDB = null;
        }
    }

    /**
     * 只有初次创建数据库文件时调用,卸载再安装或清空APP数据后也会调用
     *
     * @param db 数据库对象
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.mDB = db;
        // 清空表数据
        String dropSql = "drop table if exists " + TABLE_TOOL_CATEGORY + ";";
        Log.d(TAG, "dropSql = " + dropSql);
        db.execSQL(dropSql);
        dropSql = "drop table if exists " + TABLE_TOOL_INFO + ";";
        Log.d(TAG, "dropSql = " + dropSql);
        db.execSQL(dropSql);
        dropSql = "drop table if exists " + TABLE_SEARCH_HISTORY + ";";
        Log.d(TAG, "dropSql = " + dropSql);
        db.execSQL(dropSql);

        // 新建表
        String createQql = "create table if not exists " + TABLE_TOOL_CATEGORY + " ("
                + "category_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," //主键
                + "category_name VARCHAR(30) NOT NULL," //分类名
                + "order_id INTEGER default 1" //排序
                + ");";
        Log.d(TAG, "createQql = " + createQql);
        db.execSQL(createQql);

        createQql = "create table if not exists " + TABLE_TOOL_INFO + " ("
                + "info_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," //主键
                + "info_name VARCHAR(30)," //名称
                + "category_id integer NOT NULL constraint tool_info_tool_category_category_id_fk" +
                " references tool_category on update cascade on delete cascade," //分类id
                + "icon VARCHAR(200)," //图标
                + "address_url text," //web地址
                + "follow boolean default false," //是否收藏
                + "order_id INTEGER default 1" //排序
                + ");";
        Log.d(TAG, "createQql = " + createQql);
        db.execSQL(createQql);

        createQql = "create table if not exists " + TABLE_SEARCH_HISTORY + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," //主键
                + "search_content text," //搜索内容
                + "create_time text" //时间戳(yyyy-MM-dd HH:mm:ss)
                + ");";
        Log.d(TAG, "createQql = " + createQql);
        db.execSQL(createQql);

        // 初始化插入数据
        initArkToolData();
    }

    /**
     * 数据库升级操作,当检测到APP版本号大于本地数据库文件时调用
     *
     * @param db         数据库对象
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade oldVersion=" + oldVersion + ", newVersion=" + newVersion);
        //todo:版本号提高时,升级数据库结构或修改数据
    }

    public void insertCategory(List<ToolCategoryBean> list) {
        for (ToolCategoryBean bean : list) {
            ContentValues cv = new ContentValues();
            cv.put("category_id", bean.getCategoryId());
            cv.put("category_name", bean.getCategoryName());
            cv.put("order_id", bean.getOrderId());
            mDB.insert(TABLE_TOOL_CATEGORY, "", cv);
        }
    }

    public void insertToolInfo(List<ToolInfoBean> list) {
        for (ToolInfoBean bean : list) {
            ContentValues cv = new ContentValues();
            cv.put("info_id", bean.getInfoId());
            cv.put("info_name", bean.getInfoName());
            cv.put("category_id", bean.getCategoryId());
            cv.put("icon", bean.getIcon());
            cv.put("address_url", bean.getAddressUrl());
            cv.put("follow", bean.getFollow() ? 1 : 0);
            cv.put("order_id", bean.getOrderId());
            mDB.insert(TABLE_TOOL_INFO, "", cv);
        }
    }

    /**
     * 初始创建数据库时，添加数据
     */
    public void initArkToolData() {
        //分类
        List<ToolCategoryBean> categoryList = new ArrayList<>();
        categoryList.add(new ToolCategoryBean(-1, "全部功能", 0));
        categoryList.add(new ToolCategoryBean(ATDG_ID, "A.D.T.G 开发组", 1));
        categoryList.add(new ToolCategoryBean(2, "PRTS-方舟wiki", 2));
        categoryList.add(new ToolCategoryBean(3, "企鹅物流", 3));
        categoryList.add(new ToolCategoryBean(4, "泰拉通讯地址", 4));
        insertCategory(categoryList);
        //详情
        List<ToolInfoBean> infoList = new ArrayList<>();
        infoList.add(new ToolInfoBean(1, "抽卡分析", 1,
                "icon_glory_path", "www.baidu.com", false, 1));
        infoList.add(new ToolInfoBean(2, "干员一览", 1,
                "icon_glory_path", "www.baidu.com", false, 2));
        infoList.add(new ToolInfoBean(3, "时装回廊", 1,
                "icon_glory_path", "www.baidu.com", false, 3));
        infoList.add(new ToolInfoBean(4, "敌人一览", 1,
                "icon_glory_path", "www.baidu.com", false, 4));
        infoList.add(new ToolInfoBean(5, "资源规划", 2,
                "icon_glory_path", "www.baidu.com", false, 5));

        infoList.add(new ToolInfoBean(6, "资源规划xxxxx", 3,
                "icon_glory_path", "www.baidu.com", false, 6));
        infoList.add(new ToolInfoBean(7, "资源规划sssss", 3,
                "icon_glory_path", "www.baidu.com", false, 7));
        infoList.add(new ToolInfoBean(8, "资源规划wwwww", 3,
                "icon_glory_path", "www.baidu.com", true, 8));
        infoList.add(new ToolInfoBean(9, "资源规划", 3,
                "icon_glory_path", "www.baidu.com", false, 9));
        infoList.add(new ToolInfoBean(10, "资源规划", 3,
                "icon_glory_path", "www.baidu.com", false, 10));
        infoList.add(new ToolInfoBean(11, "资源规划", 3,
                "icon_glory_path", "www.baidu.com", false, 11));
        infoList.add(new ToolInfoBean(12, "资源规划fffff", 3,
                "icon_glory_path", "www.baidu.com", false, 12));
        infoList.add(new ToolInfoBean(13, "资源规划", 3,
                "icon_glory_path", "www.baidu.com", false, 13));
        infoList.add(new ToolInfoBean(14, "资源规划", 3,
                "icon_glory_path", "www.baidu.com", false, 14));
        infoList.add(new ToolInfoBean(15, "资源规划", 3,
                "icon_glory_path", "www.baidu.com", false, 15));
        insertToolInfo(infoList);
    }

    /**
     * 查询工具分类
     *
     * @param condition 条件
     * @return 分类列表
     */
    public List<ToolCategoryBean> queryToolCategory(String condition) {
        String sql = String.format("select * from %s where %s;", TABLE_TOOL_CATEGORY, condition);
        Log.d(TAG, "query sql: " + sql);
        ArrayList<ToolCategoryBean> result = new ArrayList<>();
        // 获得游标对象
        Cursor cursor = mDB.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            for (; ; cursor.moveToNext()) {
                ToolCategoryBean bean = new ToolCategoryBean();
                bean.setCategoryId(cursor.getInt(0));
                bean.setCategoryName(cursor.getString(1));
                bean.setOrderId(cursor.getInt(2));
                result.add(bean);
                if (cursor.isLast()) break;
            }
        }
        cursor.close();
        return result;
    }

    /**
     * 查询详情
     *
     * @param condition 条件
     * @return 详情列表
     */
    public List<ToolInfoBean> queryToolInfo(String condition) {
        String sql = String.format("select * from %s where %s;", TABLE_TOOL_INFO, condition);
        Log.d(TAG, "query sql: " + sql);
        ArrayList<ToolInfoBean> result = new ArrayList<>();
        // 获得游标对象
        Cursor cursor = mDB.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            for (; ; cursor.moveToNext()) {
                ToolInfoBean bean = new ToolInfoBean();
                bean.setInfoId(cursor.getInt(0));
                bean.setInfoName(cursor.getString(1));
                bean.setCategoryId(cursor.getInt(2));
                bean.setIcon(cursor.getString(3));
                bean.setAddressUrl(cursor.getString(4));
                bean.setFollow(cursor.getInt(5) == 1);
                bean.setOrderId(cursor.getInt(6));
                result.add(bean);
                if (cursor.isLast()) break;
            }
        }
        cursor.close();
        return result;
    }

    /**
     * 条件查询工具箱列表数据
     *
     * @param searchText 搜索内容(包含分类名称，模糊查询)
     * @param onlyFollow 查询仅收藏(false则查全部)
     * @param categoryId 查询具体分类下数据(-1查全部)
     * @return 列表数据
     */
    public List<ToolboxBean> queryToolBoxInfo(String searchText, Boolean onlyFollow, Integer categoryId) {
        String sql = "select info.category_id,category_name,info_id,info_name,icon,address_url,follow,info.order_id,category.order_id from " + TABLE_TOOL_INFO + " as info" +
                " inner join " + TABLE_TOOL_CATEGORY + " as category on info.category_id = category.category_id where 1=1 " +
                (searchText.equals("") ? "" : " and (info.info_name like '%" + searchText + "%' or category.category_name like '%" + searchText + "%')") +
                (onlyFollow ? " and info.follow == 1" : "") +
                (categoryId == -1 ? "" : " and info.category_id = " + categoryId) +
                " order by category.order_id,follow desc,info.order_id;";
        Log.d(TAG, "query sql: " + sql);
        List<ToolboxBean> result = new ArrayList<>();
        List<Integer> categoryIds = new ArrayList<>();//记录分类标签id
        // 获得游标对象
        Cursor cursor = mDB.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            for (; ; cursor.moveToNext()) {
                Integer tempCategoryId = cursor.getInt(0);
                String categoryName = cursor.getString(1);
                Integer infoId = cursor.getInt(2);
                String name = cursor.getString(3);
                String icon = cursor.getString(4);
                String addressUrl = cursor.getString(5);
                Boolean follow = cursor.getInt(6) == 1;
                Integer orderId = cursor.getInt(7);
                if (!categoryIds.contains(tempCategoryId) && categoryId == -1) {
                    //添加分类标签
                    categoryIds.add(tempCategoryId);
                    ToolboxBean bean = new ToolboxBean(tempCategoryId, categoryName, ToolboxBean.ToolInfoType.TYPE_CATEGORY);
                    result.add(bean);
                }
                ToolboxBean bean = new ToolboxBean(infoId, tempCategoryId, name, icon, addressUrl, follow, orderId, ToolboxBean.ToolInfoType.TYPE_INFO);
                result.add(bean);
                if (cursor.isLast()) break;
            }
        }
        //添加底部提示
        if (result.size() != 0) {
            ToolboxBean bean = new ToolboxBean(-10, "已经到底了", ToolboxBean.ToolInfoType.TYPE_BOTTOM);
            result.add(bean);
        }
        cursor.close();
        return result;
    }

    /**
     * 更新收藏状态
     *
     * @param infoId   对象id
     * @param ifFollow true:收藏，false:取消收藏
     */
    public void updateFollowStatus(Integer infoId, Boolean ifFollow) {
        ContentValues values = new ContentValues();
        values.put("follow", ifFollow ? 1 : 0);
        mDB.update(TABLE_TOOL_INFO, values, "info_id = ?", new String[]{String.valueOf(infoId)});
    }
}
