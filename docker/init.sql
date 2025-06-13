-- Automatically executed by MySQL container at startup
CREATE DATABASE IF NOT EXISTS cache_system;
USE cache_system;

-- Create necessary tables if they don't already exist
CREATE TABLE IF NOT EXISTS EvictionPolicy (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS LruPolicy (
    id INT AUTO_INCREMENT PRIMARY KEY,
    eviction_policy_id INT,
    FOREIGN KEY (eviction_policy_id) REFERENCES EvictionPolicy(id)
);

CREATE TABLE IF NOT EXISTS LfuPolicy (
    id INT AUTO_INCREMENT PRIMARY KEY,
    eviction_policy_id INT,
    FOREIGN KEY (eviction_policy_id) REFERENCES EvictionPolicy(id)
);

CREATE TABLE IF NOT EXISTS TtlPolicy (
    id INT AUTO_INCREMENT PRIMARY KEY,
    eviction_policy_id INT,
    FOREIGN KEY (eviction_policy_id) REFERENCES EvictionPolicy(id)
);

CREATE TABLE IF NOT EXISTS CacheManager (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS Cache (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    value TEXT NOT NULL,
    eviction_policy_id INT,
    FOREIGN KEY (eviction_policy_id) REFERENCES EvictionPolicy(id)
);

CREATE TABLE IF NOT EXISTS CacheManager_Cache (
    cache_manager_id INT,
    cache_id INT,
    PRIMARY KEY (cache_manager_id, cache_id),
    FOREIGN KEY (cache_manager_id) REFERENCES CacheManager(id),
    FOREIGN KEY (cache_id) REFERENCES Cache(id)
);

CREATE TABLE IF NOT EXISTS Exception (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    cache_id INT,
    FOREIGN KEY (cache_id) REFERENCES Cache(id)
);

-- Insert Eviction Policies
INSERT INTO EvictionPolicy (name, description) VALUES ('LRU', 'Least Recently Used eviction policy');
INSERT INTO EvictionPolicy (name, description) VALUES ('LFU', 'Least Frequently Used eviction policy');
INSERT INTO EvictionPolicy (name, description) VALUES ('TTL', 'Time-to-live eviction policy');

-- Link to sub-policy tables
INSERT INTO LruPolicy (eviction_policy_id) VALUES (1);
INSERT INTO LfuPolicy (eviction_policy_id) VALUES (2);
INSERT INTO TtlPolicy (eviction_policy_id) VALUES (3);

-- Cache Manager
INSERT INTO CacheManager (name) VALUES ('Default Manager');

-- Cache entries (FIXED: changed `key` -> name)
INSERT INTO Cache (name, value, eviction_policy_id) VALUES ('cache:lru:1', 'LRU Test Data', 1);
INSERT INTO Cache (name, value, eviction_policy_id) VALUES ('cache:lfu:1', 'LFU Test Data', 2);
INSERT INTO Cache (name, value, eviction_policy_id) VALUES ('cache:ttl:1', 'TTL Test Data', 3);

-- Linking caches to manager
INSERT INTO CacheManager_Cache (cache_manager_id, cache_id) VALUES (1, 1);
INSERT INTO CacheManager_Cache (cache_manager_id, cache_id) VALUES (1, 2);
INSERT INTO CacheManager_Cache (cache_manager_id, cache_id) VALUES (1, 3);

-- Exceptions
INSERT INTO Exception (type, message, cache_id) VALUES ('CacheFullException', 'LRU cache is full', 1);
INSERT INTO Exception (type, message, cache_id) VALUES ('ItemNotFoundException', 'Item not found in LFU cache', 2);
