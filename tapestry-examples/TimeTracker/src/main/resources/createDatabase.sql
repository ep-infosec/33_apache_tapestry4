create table projects (
	project_id		int				generated by default as IDENTITY not null,
	name			varchar(40)		not null,
	PRIMARY KEY (project_id),
	UNIQUE (name)
);

insert into projects(name) values ('Code Zeitgeist');
insert into projects(name) values ('Zooland Systems');
insert into projects(name) values ('Weedasher Industries');

create table tasks (
	task_id			int				generated by default as IDENTITY not null,
	project_id		int				not null,
	start_dt		timestamp		not null,
	end_dt			timestamp		not null,
	descr_txt		varchar(200)	not null,
	PRIMARY KEY (task_id),
	FOREIGN KEY (project_id) references projects(project_id)
);
