Select s.name As name, s.description as description, t.name as tag, s.status, s.lastmodifiedby, s.lastmodifieddatetime, s.domain
from Strategy s, tagmbr tm, tag t
where s.id = tm.strategytagmbrlhs_id
and tm.tagrhs_id = t.id
