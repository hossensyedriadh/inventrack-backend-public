create table customers
(
    name     text         not null,
    phone_no varchar(20)  not null
        primary key,
    email    varchar(150) null,
    address  text         not null
);

create table delivery_mediums
(
    name varchar(150) not null
        primary key
);

create table payment_methods
(
    name varchar(150) not null
        primary key
);

create table product_categories
(
    name varchar(150) not null
        primary key
);

create table profiles
(
    profile_id varchar(36)  not null
        primary key,
    first_name text         not null,
    last_name  text         not null,
    email      varchar(150) not null,
    phone_no   varchar(20)  null,
    user_since date         not null,
    avatar     text         null,
    constraint email
        unique (email)
);

create table users
(
    username          varchar(75)                                                not null
        primary key,
    password          text                                                       not null,
    authority         enum ('ROLE_ROOT', 'ROLE_ADMINISTRATOR', 'ROLE_MODERATOR') not null,
    is_enabled        tinyint(1) default 1                                       not null,
    is_not_locked     tinyint(1) default 1                                       not null,
    profile_reference varchar(36)                                                not null,
    constraint users_ibfk_1
        foreign key (profile_reference) references profiles (profile_id)
            on update cascade on delete cascade
);

create table refresh_tokens
(
    id       varchar(36) not null
        primary key,
    token    text        not null,
    for_user varchar(75) not null,
    constraint refresh_tokens_ibfk_1
        foreign key (for_user) references users (username)
);

create index for_user
    on refresh_tokens (for_user);

create table sales
(
    id              varchar(20)                                           not null
        primary key,
    total_payable   double                                                not null,
    total_due       double    default 0                                   not null,
    customer        varchar(20)                                           not null,
    payment_status  enum ('PENDING', 'COMPLETED', 'PARTIAL', 'CANCELLED') not null,
    payment_method  varchar(150)                                          not null,
    payment_details text                                                  null,
    order_status    enum ('PENDING', 'CONFIRMED', 'CANCELLED')            not null,
    delivery_medium varchar(150)                                          not null,
    notes           text                                                  null,
    added_by        varchar(75)                                           not null,
    added_on        timestamp default CURRENT_TIMESTAMP                   not null,
    updated_by      varchar(75)                                           null,
    updated_on      timestamp                                             null,
    constraint sales_ibfk_2
        foreign key (payment_method) references payment_methods (name),
    constraint sales_ibfk_3
        foreign key (delivery_medium) references delivery_mediums (name),
    constraint sales_ibfk_4
        foreign key (added_by) references users (username),
    constraint sales_ibfk_5
        foreign key (updated_by) references users (username),
    constraint sales_ibfk_6
        foreign key (customer) references customers (phone_no)
);

create index added_by
    on sales (added_by);

create index customer
    on sales (customer);

create index delivery_medium
    on sales (delivery_medium);

create index payment_method
    on sales (payment_method);

create index updated_by
    on sales (updated_by);

create table saved_codes
(
    id         varchar(16) not null
        primary key,
    code       varchar(8)  not null,
    expires_on timestamp   not null,
    for_user   varchar(75) not null,
    constraint saved_codes_ibfk_1
        foreign key (for_user) references users (username)
);

create index for_user
    on saved_codes (for_user);

create table signup_invitations
(
    id                   varchar(16)                                   not null
        primary key,
    token                varchar(73)                                   not null,
    created_on           timestamp default CURRENT_TIMESTAMP           not null,
    expires_on           timestamp                                     not null,
    status               enum ('VALID', 'INVALID')                     not null,
    invalidation_remarks enum ('USED', 'EXPIRED', 'REVOKED')           null,
    invalidated_on       timestamp                                     null,
    recipient_email      varchar(125)                                  not null,
    for_authority        enum ('ROLE_ADMINISTRATOR', 'ROLE_MODERATOR') not null,
    created_by           varchar(75)                                   not null,
    constraint token
        unique (token),
    constraint signup_invitations_ibfk_1
        foreign key (created_by) references users (username)
);

create index created_by
    on signup_invitations (created_by);

create table suppliers
(
    name       text                                not null,
    phone_no   varchar(20)                         not null
        primary key,
    email      varchar(150)                        null,
    address    text                                not null,
    website    text                                null,
    notes      text                                null,
    added_on   timestamp default CURRENT_TIMESTAMP not null,
    added_by   varchar(75)                         not null,
    updated_by varchar(75)                         null,
    updated_on timestamp                           null,
    constraint suppliers_ibfk_1
        foreign key (added_by) references users (username),
    constraint suppliers_ibfk_2
        foreign key (updated_by) references users (username)
);

create table purchase_orders
(
    id                   varchar(20)                               not null
        primary key,
    name                 text                                      not null,
    category             varchar(150)                              not null,
    specifications       text                                      null,
    quantity             int                                       not null,
    total_purchase_price double                                    not null,
    shipping_costs       double                                    not null,
    other_costs          double                                    not null,
    selling_price        double                                    not null,
    added_on             timestamp default CURRENT_TIMESTAMP       not null,
    supplier_reference   varchar(20)                               not null,
    status               enum ('PENDING', 'IN_STOCK', 'CANCELLED') not null,
    type                 enum ('NEW_PRODUCT', 'RESTOCK')           not null,
    product_id           varchar(16)                               null,
    added_by             varchar(75)                               not null,
    updated_by           varchar(75)                               null,
    updated_on           timestamp                                 null,
    constraint purchase_orders_ibfk_1
        foreign key (category) references product_categories (name),
    constraint purchase_orders_ibfk_3
        foreign key (added_by) references users (username),
    constraint purchase_orders_ibfk_4
        foreign key (updated_by) references users (username),
    constraint purchase_orders_ibfk_5
        foreign key (supplier_reference) references suppliers (phone_no)
);

create table finance_records
(
    id                 int auto_increment
        primary key,
    month              int                      not null,
    year               int                      not null,
    value              double                   not null,
    record_type        enum ('EXPENSE', 'SALE') not null,
    purchase_order_ref varchar(20)              null,
    sale_order_ref     varchar(20)              null,
    constraint purchase_order_ref
        unique (purchase_order_ref),
    constraint sale_order_ref
        unique (sale_order_ref),
    constraint finance_records_ibfk_1
        foreign key (purchase_order_ref) references purchase_orders (id),
    constraint finance_records_ibfk_2
        foreign key (sale_order_ref) references sales (id)
);

create table products
(
    id                 varchar(16)  not null
        primary key,
    name               text         not null,
    category           varchar(150) not null,
    specifications     text         null,
    stock              int          not null,
    price              double       not null,
    updated_by         varchar(75)  null,
    updated_on         timestamp    null,
    purchase_order_ref varchar(20)  not null,
    constraint products_ibfk_1
        foreign key (category) references product_categories (name),
    constraint products_ibfk_2
        foreign key (updated_by) references users (username),
    constraint products_ibfk_3
        foreign key (purchase_order_ref) references purchase_orders (id)
);

create table product_images
(
    tag            varchar(36) not null
        primary key,
    static_cdn_url text        not null,
    for_product    varchar(16) not null,
    constraint product_images_ibfk_1
        foreign key (for_product) references products (id)
);

create index for_product
    on product_images (for_product);

create index category
    on products (category);

create index purchase_order_ref
    on products (purchase_order_ref);

create index updated_by
    on products (updated_by);

create index added_by
    on purchase_orders (added_by);

create index category
    on purchase_orders (category);

create index supplier_reference
    on purchase_orders (supplier_reference);

create index updated_by
    on purchase_orders (updated_by);

create table sale_items
(
    id       varchar(36) not null
        primary key,
    product  varchar(16) not null,
    quantity int         not null,
    price    double      not null,
    sale_ref varchar(20) not null,
    constraint sale_items_ibfk_1
        foreign key (product) references products (id),
    constraint sale_items_ibfk_2
        foreign key (sale_ref) references sales (id)
);

create index product
    on sale_items (product);

create index sale_ref
    on sale_items (sale_ref);

create index profile_reference
    on users (profile_reference);