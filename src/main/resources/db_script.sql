CREATE TABLE Post (
    user_id BIGINT,
    post_id BIGINT,
    site_name VARCHAR(255),
    PRIMARY KEY(user_id, post_id, site_name)
);
