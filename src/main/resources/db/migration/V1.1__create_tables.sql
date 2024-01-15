create table if not exists item(
    id serial not null primary key,
    version int not null default 1,
    status varchar(15) not null,
    description varchar(4000) not null,
    created_date timestamp default CURRENT_TIMESTAMP not null,
    last_modified_date timestamp default CURRENT_TIMESTAMP not null
);