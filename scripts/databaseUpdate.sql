#UPDATE weekly_total W JOIN monthly_total_weekly_total M ON W.id=M.weekly_total_id SET W.monthly_total_id = M.monthly_total_weekly_totals_id;
		
#UPDATE in_and_out IO JOIN daily_total DT ON DT.day=IO.day AND DT.month=IO.month AND DT.year=IO.year AND DT.employee_id=IO.employee_id SET IO.daily_total_id=DT.id;


UPDATE daily_total D JOIN weekly_total W ON W.id=D.weekly_total_id SET D.week = W.week;
