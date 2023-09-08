package com.suntend.arktoolbox.database.helper;

import static com.suntend.arktoolbox.fragment.toolbox.ToolBoxFragment.LIST_TYPE_KEY;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.suntend.arktoolbox.database.bean.ConfigValue;
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
    public static final int ATDG_ID = 1;//ATDG的分类id，用于显示判断所以固定
    private static ArkToolDatabaseHelper mHelper = null;//单例
    private SQLiteDatabase mDB = null;//数据库对象
    private static final String TABLE_TOOL_CATEGORY = "tool_category";//工具分类表
    private static final String TABLE_TOOL_INFO = "tool_info";//工具详情表
    private static final String TABLE_SEARCH_HISTORY = "search_history";//搜索历史表
    private static final String TABLE_CONFIG_VALUE = "config_value";//配置键值对表，各种散装配置

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
        dropSql = "drop table if exists " + TABLE_CONFIG_VALUE + ";";
        Log.d(TAG, "dropSql = " + dropSql);
        db.execSQL(dropSql);
        /*dropSql = "drop table if exists " + TABLE_SEARCH_HISTORY + ";";
        Log.d(TAG, "dropSql = " + dropSql);
        db.execSQL(dropSql);*/

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

        createQql = "create table if not exists " + TABLE_CONFIG_VALUE + " ("
                + "config_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," //主键
                + "config_key VARCHAR(255) UNIQUE," //key值
                + "config_value text" //value值
                + ");";
        Log.d(TAG, "createQql = " + createQql);
        db.execSQL(createQql);

        /*createQql = "create table if not exists " + TABLE_SEARCH_HISTORY + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," //主键
                + "search_content text," //搜索内容
                + "create_time text" //时间戳(yyyy-MM-dd HH:mm:ss)
                + ");";
        Log.d(TAG, "createQql = " + createQql);
        db.execSQL(createQql);*/

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
     * 插入键值对
     */
    public void insertConfigValue(List<ConfigValue> list) {
        for (ConfigValue bean : list) {
            ContentValues cv = new ContentValues();
            cv.put("config_id", bean.getConfigId());
            cv.put("config_key", bean.getConfigKey());
            cv.put("config_value", bean.getConfigValue());
            mDB.insert(TABLE_CONFIG_VALUE, "", cv);
        }
    }

    /**
     * 初始创建数据库时，添加数据
     */
    public void initArkToolData() {
        //分类
        List<ToolCategoryBean> categoryList = new ArrayList<>();
        categoryList.add(new ToolCategoryBean(-1, "全部功能", 0));
        categoryList.add(new ToolCategoryBean(ATDG_ID, "终端工具", 1));
        categoryList.add(new ToolCategoryBean(2, "PRTS wiki", 2));
        categoryList.add(new ToolCategoryBean(3, "企鹅物流", 3));
        categoryList.add(new ToolCategoryBean(4, "方舟计算器", 4));
        categoryList.add(new ToolCategoryBean(5, "其他", 5));
        insertCategory(categoryList);
        //详情
        List<ToolInfoBean> infoList = new ArrayList<>();
        infoList.add(new ToolInfoBean(1, "抽卡分析", ATDG_ID, "icon_tool_draw_card", "www.baidu.com", false, 1));


        infoList.add(new ToolInfoBean(2, "PRTS-方舟wiki首页", 2, "icon_tool_main_page", "https://prts.wiki/", false, 1));
        infoList.add(new ToolInfoBean(3, "干员一览", 2, "icon_tool_operator_overview", "https://prts.wiki/w/%E5%B9%B2%E5%91%98%E4%B8%80%E8%A7%88", false, 2));
        infoList.add(new ToolInfoBean(4, "敌人一览", 2, "icon_tool_enemy_overview", "https://prts.wiki/w/%E6%95%8C%E4%BA%BA%E4%B8%80%E8%A7%88", false, 3));
        infoList.add(new ToolInfoBean(5, "道具一览", 2, "icon_tool_item_overview", "https://prts.wiki/w/%E9%81%93%E5%85%B7%E4%B8%80%E8%A7%88", false, 4));
        infoList.add(new ToolInfoBean(6, "后勤技能", 2, "icon_tool_temp", "https://prts.wiki/w/%E5%90%8E%E5%8B%A4%E6%8A%80%E8%83%BD%E4%B8%80%E8%A7%88", false, 5));
        infoList.add(new ToolInfoBean(7, "关卡一览", 2, "icon_tool_stage_overview", "https://prts.wiki/w/%E5%85%B3%E5%8D%A1%E4%B8%80%E8%A7%88", false, 6));
        infoList.add(new ToolInfoBean(8, "剧情记录", 2, "icon_tool_story_record", "https://prts.wiki/w/%E5%89%A7%E6%83%85%E4%B8%80%E8%A7%88", false, 7));
        infoList.add(new ToolInfoBean(9, "家具一览", 2, "icon_tool_furniture_overview", "https://prts.wiki/w/%E5%AE%B6%E5%85%B7%E4%B8%80%E8%A7%88", false, 8));
        infoList.add(new ToolInfoBean(10, "时装回廊", 2, "icon_tool_dress_overview", "https://prts.wiki/w/%E6%97%B6%E8%A3%85%E5%9B%9E%E5%BB%8A", false, 9));
        infoList.add(new ToolInfoBean(11, "采购中心", 2, "icon_tool_cute_closure", "https://prts.wiki/w/%E9%87%87%E8%B4%AD%E4%B8%AD%E5%BF%83", false, 10));
        infoList.add(new ToolInfoBean(12, "卡池一览", 2, "icon_tool_card_pool_overview", "https://prts.wiki/w/%E5%8D%A1%E6%B1%A0%E4%B8%80%E8%A7%88", false, 11));
        infoList.add(new ToolInfoBean(13, "活动一览", 2, "icon_tool_activity_overview", "https://prts.wiki/w/%E6%B4%BB%E5%8A%A8%E4%B8%80%E8%A7%88", false, 12));
        infoList.add(new ToolInfoBean(14, "公招计算", 2, "icon_tool_public_recruitment", "https://prts.wiki/w/%E5%85%AC%E6%8B%9B%E8%AE%A1%E7%AE%97", false, 13));
        infoList.add(new ToolInfoBean(15, "画师一览", 2, "icon_tool_artist_overview", "https://prts.wiki/w/%E7%94%BB%E5%B8%88%E4%B8%80%E8%A7%88", false, 14));
        infoList.add(new ToolInfoBean(16, "配音一览", 2, "icon_tool_cv_overview", "https://prts.wiki/w/%E9%85%8D%E9%9F%B3%E4%B8%80%E8%A7%88", false, 15));
        infoList.add(new ToolInfoBean(17, "干员分支", 2, "icon_tool_temp", "https://prts.wiki/w/%E5%88%86%E6%94%AF%E4%B8%80%E8%A7%88", false, 16));
        infoList.add(new ToolInfoBean(18, "干员密录", 2, "icon_tool_operator_secret", "https://prts.wiki/w/%E5%B9%B2%E5%91%98%E5%AF%86%E5%BD%95%E4%B8%80%E8%A7%88", false, 17));
        infoList.add(new ToolInfoBean(19, "干员模组", 2, "icon_tool_operator_module", "https://prts.wiki/w/%E5%B9%B2%E5%91%98%E6%A8%A1%E7%BB%84%E4%B8%80%E8%A7%88", false, 18));
        infoList.add(new ToolInfoBean(20, "干员编号", 2, "icon_tool_operator_number", "https://prts.wiki/w/%E5%B9%B2%E5%91%98%E7%BC%96%E5%8F%B7%E4%B8%80%E8%A7%88", false, 19));
        infoList.add(new ToolInfoBean(21, "角色真名", 2, "icon_tool_character_real_name", "https://prts.wiki/w/%E8%A7%92%E8%89%B2%E7%9C%9F%E5%90%8D", false, 20));
        infoList.add(new ToolInfoBean(22, "干员预告", 2, "icon_tool_operator_in_advance", "https://prts.wiki/w/%E5%B9%B2%E5%91%98%E9%A2%84%E5%91%8A", false, 21));
        infoList.add(new ToolInfoBean(23, "干员专精", 2, "icon_tool_temp", "https://prts.wiki/w/%E5%B9%B2%E5%91%98%E4%B8%93%E7%B2%BE", false, 22));
        infoList.add(new ToolInfoBean(24, "干员剧情", 2, "icon_tool_operator_story", "https://prts.wiki/w/%E5%B9%B2%E5%91%98%E5%89%A7%E6%83%85%E4%B8%80%E8%A7%88", false, 23));
        infoList.add(new ToolInfoBean(25, "档案信息", 2, "icon_tool_archives_overview", "https://prts.wiki/w/%E6%A1%A3%E6%A1%88%E4%BF%A1%E6%81%AF%E4%B8%80%E8%A7%88", false, 24));
        infoList.add(new ToolInfoBean(26, "危机合约", 2, "icon_tool_crisis_contract", "https://prts.wiki/w/%E5%8D%B1%E6%9C%BA%E5%90%88%E7%BA%A6", false, 25));
        infoList.add(new ToolInfoBean(27, "集成战略", 2, "icon_tool_integrated_strategy", "https://prts.wiki/w/%E9%9B%86%E6%88%90%E6%88%98%E7%95%A5", false, 26));
        infoList.add(new ToolInfoBean(28, "多维合作", 2, "icon_tool_dimension_cooperate", "https://prts.wiki/w/%E5%A4%9A%E7%BB%B4%E5%90%88%E4%BD%9C", false, 27));
        infoList.add(new ToolInfoBean(29, "联锁竞赛", 2, "icon_tool_temp", "https://prts.wiki/w/%E8%8D%B7%E8%B0%9F%E4%BC%8A%E6%99%BA%E5%A2%83", false, 28));
        infoList.add(new ToolInfoBean(30, "保全派驻", 2, "icon_tool_temp", "https://prts.wiki/w/%E4%BF%9D%E5%85%A8%E6%B4%BE%E9%A9%BB", false, 29));
        infoList.add(new ToolInfoBean(31, "引航者试炼", 2, "icon_tool_temp", "https://prts.wiki/w/%E5%BC%95%E8%88%AA%E8%80%85%E8%AF%95%E7%82%BC", false, 30));
        infoList.add(new ToolInfoBean(32, "生息演算", 2, "icon_tool_temp", "https://prts.wiki/w/%E7%94%9F%E6%81%AF%E6%BC%94%E7%AE%97", false, 31));
        infoList.add(new ToolInfoBean(33, "尖灭作战", 2, "icon_tool_temp", "https://prts.wiki/w/%E5%B0%96%E7%81%AD%E6%B5%8B%E8%AF%95%E4%BD%9C%E6%88%98", false, 32));
        infoList.add(new ToolInfoBean(34, "基建数据", 2, "icon_tool_infrastructure", "https://prts.wiki/w/%E7%BD%97%E5%BE%B7%E5%B2%9B%E5%9F%BA%E5%BB%BA", false, 33));
        infoList.add(new ToolInfoBean(35, "任务列表", 2, "icon_tool_task_list", "https://prts.wiki/w/%E4%BB%BB%E5%8A%A1%E5%88%97%E8%A1%A8", false, 34));
        infoList.add(new ToolInfoBean(36, "邮件记录", 2, "icon_tool_mail_record", "https://prts.wiki/w/%E9%82%AE%E4%BB%B6%E8%AE%B0%E5%BD%95", false, 35));
        infoList.add(new ToolInfoBean(37, "场景一览", 2, "icon_tool_temp", "https://prts.wiki/w/%E9%A6%96%E9%A1%B5%E5%9C%BA%E6%99%AF%E4%B8%80%E8%A7%88", false, 36));
        infoList.add(new ToolInfoBean(38, "头像名片", 2, "icon_tool_temp", "https://prts.wiki/w/%E4%B8%AA%E4%BA%BA%E5%90%8D%E7%89%87%E5%A4%B4%E5%83%8F%E4%B8%80%E8%A7%88", false, 37));
        infoList.add(new ToolInfoBean(39, "光荣之路", 2, "icon_tool_glory_path", "https://prts.wiki/w/%E5%85%89%E8%8D%A3%E4%B9%8B%E8%B7%AF", false, 38));
        infoList.add(new ToolInfoBean(40, "情报处理室", 2, "icon_tool_information_processing", "https://prts.wiki/w/%E6%83%85%E6%8A%A5%E5%A4%84%E7%90%86%E5%AE%A4", false, 39));
        infoList.add(new ToolInfoBean(41, "制作组通讯", 2, "icon_tool_production_team_communication", "https://prts.wiki/w/%E5%88%B6%E4%BD%9C%E7%BB%84%E9%80%9A%E8%AE%AF", false, 40));
        infoList.add(new ToolInfoBean(42, "剧情角色", 2, "icon_tool_actor_overview", "https://prts.wiki/w/%E5%89%A7%E6%83%85%E8%A7%92%E8%89%B2%E4%B8%80%E8%A7%88", false, 41));
        infoList.add(new ToolInfoBean(43, "内容前瞻", 2, "icon_tool_content_foresight", "https://prts.wiki/w/%E6%B8%B8%E6%88%8F%E5%86%85%E5%AE%B9%E5%89%8D%E7%9E%BB", false, 42));
        infoList.add(new ToolInfoBean(44, "剧情资源", 2, "icon_tool_story_resource", "https://prts.wiki/w/%E5%89%A7%E6%83%85%E8%B5%84%E6%BA%90%E6%A6%82%E8%A7%88", false, 43));
        infoList.add(new ToolInfoBean(45, "泰拉大典", 2, "icon_tool_grand_ceremony", "https://prts.wiki/w/%E6%B3%B0%E6%8B%89%E5%A4%A7%E5%85%B8:Index", false, 44));
        infoList.add(new ToolInfoBean(46, "音乐鉴赏", 2, "icon_tool_siren_music", "https://prts.wiki/w/%E9%9F%B3%E4%B9%90%E9%89%B4%E8%B5%8F", false, 45));
        infoList.add(new ToolInfoBean(47, "寻访模拟", 2, "icon_tool_temp", "https://prts.wiki/w/%E5%88%86%E7%B1%BB:%E5%AF%BB%E8%AE%BF%E6%A8%A1%E6%8B%9F", false, 46));
        infoList.add(new ToolInfoBean(48, "衍生作品", 2, "icon_tool_derive_works", "https://prts.wiki/w/%E8%A1%8D%E7%94%9F%E4%BD%9C%E5%93%81", false, 47));


        infoList.add(new ToolInfoBean(49, "物流首页", 3, "icon_tool_penguin_logistics", "https://penguin-stats.cn/", false, 1));
        infoList.add(new ToolInfoBean(50, "素材掉率", 3, "icon_tool_material_drop_rate", "https://penguin-stats.cn/result/item", false, 2));
        infoList.add(new ToolInfoBean(51, "作战掉率", 3, "icon_tool_battle_drop_rate", "https://penguin-stats.cn/result/stage", false, 3));
        infoList.add(new ToolInfoBean(52, "截图汇报", 3, "icon_tool_screenshot_recognise", "https://penguin-stats.cn/report/recognition", false, 4));
        infoList.add(new ToolInfoBean(53, "作战汇报", 3, "icon_tool_battle_report", "https://penguin-stats.cn/report/stage", false, 5));
        infoList.add(new ToolInfoBean(54, "刷图规划器", 3, "icon_tool_get_resource_plan", "https://penguin-stats.cn/planner", false, 6));
        infoList.add(new ToolInfoBean(55, "高级查询", 3, "icon_tool_advance_search", "https://penguin-stats.cn/advanced", false, 7));
        infoList.add(new ToolInfoBean(56, "全站数据", 3, "icon_tool_total_data_overview", "https://penguin-stats.cn/statistics", false, 8));


        infoList.add(new ToolInfoBean(57, "公招计算", 4, "icon_tool_public_recruitment_2", "https://arkn.lolicon.app/#/hr", false, 1));
        infoList.add(new ToolInfoBean(58, "材料计算", 4, "icon_tool_elite_material_compute", "https://arkn.lolicon.app/#/material", false, 2));
        infoList.add(new ToolInfoBean(59, "升级计算", 4, "icon_tool_operator_levelup_compute", "https://arkn.lolicon.app/#/level", false, 3));
        infoList.add(new ToolInfoBean(60, "基建计算", 4, "icon_tool_construction_skill", "https://arkn.lolicon.app/#/riic", false, 4));
        infoList.add(new ToolInfoBean(61, "仓库导入", 4, "icon_tool_warehouse_material_import", "https://arkn.lolicon.app/#/depot", false, 5));


        infoList.add(new ToolInfoBean(62, "泰拉通讯枢纽", 5, "icon_tool_hub", "https://terrach.net/", false, 1));

        insertToolInfo(infoList);

        //键值对
        List<ConfigValue> configValueList = new ArrayList<>();
        configValueList.add(new ConfigValue(LIST_TYPE_KEY, "list"));//工具箱的列表显示模式  list：列表  grid：网格
        insertConfigValue(configValueList);
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

    /**
     * 更新键值对
     *
     * @param configKey   key值
     * @param configValue value值
     */
    public void updateConfigKey(String configKey, String configValue) {
        ContentValues values = new ContentValues();
        values.put("config_value", configValue);
        mDB.update(TABLE_CONFIG_VALUE, values, "config_key = ?", new String[]{configKey});
    }

    /**
     * 根据键值对的key值查询value
     *
     * @param configKye key值
     * @return value值
     */
    public String getConfigValue(String configKye) {
        String sql = String.format("select config_value from %s where config_key = '%s';", TABLE_CONFIG_VALUE, configKye);
        Log.d(TAG, "query sql: " + sql);
        String result = "";
        // 获得游标对象
        Cursor cursor = mDB.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            for (; ; cursor.moveToNext()) {
                result = cursor.getString(0);
                if (cursor.isLast()) break;
            }
        }
        cursor.close();
        return result;
    }
}
