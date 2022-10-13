-- apply changes
create table url (
  id                            bigint generated by default as identity not null,
  name                          varchar(255) not null,
  created_at                    timestamp not null,
  constraint pk_url primary key (id)
);

create table url_check (
  id                            bigint generated by default as identity not null,
  status_code                   integer not null,
  title                         varchar(255),
  h1                            varchar(255),
  description                   clob,
  url_id                        bigint,
  created_at                    timestamp not null,
  constraint pk_url_check primary key (id)
);

create index ix_url_check_url_id on url_check (url_id);
alter table url_check add constraint fk_url_check_url_id foreign key (url_id) references url (id) on delete restrict on update restrict;

