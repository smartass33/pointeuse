#UPDATE weekly_total W JOIN monthly_total_weekly_total M ON W.id=M.weekly_total_id SET W.monthly_total_id = M.monthly_total_weekly_totals_id;
		
#UPDATE in_and_out IO JOIN daily_total DT ON DT.day=IO.day AND DT.month=IO.month AND DT.year=IO.year AND DT.employee_id=IO.employee_id SET IO.daily_total_id=DT.id;


update weekly_total set monthly_total_id=13 where id=36;
update weekly_total set monthly_total_id=121 where id=312;
update weekly_total set monthly_total_id=134 where id=314;
update weekly_total set monthly_total_id=122 where id=331;
update weekly_total set monthly_total_id=127 where id=335;
update weekly_total set monthly_total_id=129 where id=337;
update weekly_total set monthly_total_id=86 where id=354;
update weekly_total set monthly_total_id=139 where id=366;