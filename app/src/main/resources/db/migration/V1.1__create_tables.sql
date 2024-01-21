create table if not exists person(
    id serial not null primary key,
    version int not null default 1,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    created_date timestamp default CURRENT_TIMESTAMP not null,
    last_modified_date timestamp default CURRENT_TIMESTAMP not null
);

create table if not exists item(
    id serial not null primary key,
    version int not null default 1,
    status varchar(15) not null,
    description varchar(4000) not null,
    assignee_id int,
    constraint fk_person foreign key (assignee_id) references person (id),
    created_date timestamp default CURRENT_TIMESTAMP not null,
    last_modified_date timestamp default CURRENT_TIMESTAMP not null
);

create table if not exists tag(
    id serial not null primary key,
    version int not null default 1,
    name varchar(50) not null,
    created_date timestamp default CURRENT_TIMESTAMP not null,
    last_modified_date timestamp default CURRENT_TIMESTAMP not null
);

create table if not exists item_tag(
    id serial not null primary key,
    item_id int not null,
    tag_id int not null,
    constraint fk_item foreign key (item_id) references item (id),
    constraint fk_tag foreign key (tag_id) references tag (id)
);

create table if not exists item_person(
    id serial not null primary key,
    item_id int not null,
    person_id int not null,
    constraint fk_item foreign key (item_id) references item (id),
    constraint fk_person foreign key (person_id) references person (id)
);