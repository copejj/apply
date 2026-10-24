BEGIN;
update users t1
set password_hash = t2.password_hash
from (
	values (1, '$argon2i$v=19$m=65536,t=4,p=1$TUhMTXhXSDZZR1J3c3JUUA$T+2vDfmO7TuJhG2yKpU9LGjn9VAX6YdYb1PmbtLe3JE'),
		(3, '$argon2i$v=19$m=65536,t=4,p=1$TUhMTXhXSDZZR1J3c3JUUA$T+2vDfmO7TuJhG2yKpU9LGjn9VAX6YdYb1PmbtLe3JE')
) as t2(user_id, password_hash)
where t1.user_id = t2.user_id;
COMMIT;