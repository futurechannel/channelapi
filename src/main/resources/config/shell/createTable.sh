host="127.0.0.1"
port="3306"
user="root"
password="root"


dbName="channel_data"

tableName="report_log"

tableName_new=${tableName}_`date -d "2 day" +%Y%m%d`

tableName_old=${tableName}_`date "+%Y%m%d"`

create_table_sql="create table if not exists ${tableName_new} like ${tableName_old};"

echo `date "+%Y-%m-%d %H:%M:%S"` "sql:${create_table_sql}" >> /channelapp/shell/execute.log

mysql -h ${host} -P ${port} -u ${user} -p${password} -D ${dbName} -e "${create_table_sql}"
if [ $? = 0 ];then
    echo `date "+%Y-%m-%d %H:%M:%S"` "create ${tableName_new} suc" >> /channelapp/shell/execute.log
    else
    echo `date "+%Y-%m-%d %H:%M:%S"` "create ${tableName_new} fail" >> /channelapp/shell/execute.log
fi