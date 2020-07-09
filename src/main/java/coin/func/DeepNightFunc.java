package coin.func;

import coin.annotation.Func;
import coin.func.base.BaseFunc;
import coin.func.base.FuncResult;
import coin.log.LogFile;
import coin.utils.DBOperator;

/**
 * @author Qian
 * @date 2020/4/27
 */
@Func(value="deepNight", desc="深夜")
public class DeepNightFunc extends BaseFunc {

    private DBOperator dbOperator;
    private LogFile logFile;

    @Override
    protected boolean isIllegalArgs(String[] args) {
        return false;
    }

    @Override
    protected FuncResult action(String[] args) {

        dbOperator = DBOperator.getDBOperator(DBOperator.Env.PRODUCT_R_W);
        logFile = LogFile.getInstance(this.getClass());

        String sql = "select count(*) from es_order_extvtl t " +
                "        where t.is_refresh is null " +
                "           and exists (select a.order_id from es_order_items_ext a " +
                "                 where a.order_id = t.order_id and a.goods_cat_id = '20218001')" +
                "            and exists (select oe.order_id from es_order_ext oe " +
                "                where oe.order_id = t.order_id " +
                "                      and oe.tid_time > sysdate - 1" +
                "                      and oe.flow_trace_id = 'F')";

        String updateSql = "update es_order_extvtl t set t.is_refresh = '1' " +
                "       where t.is_refresh is null " +
                "        and exists (select a.order_id from es_order_items_ext a " +
                "             where a.order_id = t.order_id and a.goods_cat_id = '20218001') " +
                "        and exists (select oe.order_id from es_order_ext oe " +
                "            where oe.order_id = t.order_id " +
                "                  and oe.tid_time > sysdate - 1 " +
                "                  and oe.flow_trace_id = 'F')";

        int count = Integer.parseInt(dbOperator.queryForString(sql));
        logFile.log2File(">> count : " + count);
        if (count > 0) {
            int execute = dbOperator.execute(updateSql);
            logFile.log2File(">> effect row : " + execute);
        }

        return null;
    }
}
