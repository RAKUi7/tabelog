-- categoriesテーブル
INSERT IGNORE INTO categories (id, name) VALUES (1, '洋食');
INSERT IGNORE INTO categories (id, name) VALUES (2, '和食');
INSERT IGNORE INTO categories (id, name) VALUES (3, 'イタリアン');
INSERT IGNORE INTO categories (id, name) VALUES (4, 'フレンチ');
INSERT IGNORE INTO categories (id, name) VALUES (5, '中華料理');
INSERT IGNORE INTO categories (id, name) VALUES (6, '寿司');
INSERT IGNORE INTO categories (id, name) VALUES (7, '海鮮');
INSERT IGNORE INTO categories (id, name) VALUES (8, 'スペイン料理');
INSERT IGNORE INTO categories (id, name) VALUES (9, '韓国料理');
INSERT IGNORE INTO categories (id, name) VALUES (10, '焼肉');
INSERT IGNORE INTO categories (id, name) VALUES (11, '鍋');
INSERT IGNORE INTO categories (id, name) VALUES (12, 'カフェ');
INSERT IGNORE INTO categories (id, name) VALUES (13, 'デザート');


--restaurantsテーブル
INSERT IGNORE INTO restaurants (id, name, image_name, description, lowest_price, highest_price, postal_code, address, phone_number, opening_time, closing_time, category_id) VALUES (1, '名古屋うどん', 'restaurant1.jpg', '最寄り駅から徒歩10分。美味しいよ', 1000, 4000, '073-0145', '名古屋１', '012-345-678', '9:00', '21:00', 1); 
INSERT IGNORE INTO restaurants (id, name, image_name, description, lowest_price, highest_price, postal_code, address, phone_number, opening_time, closing_time, category_id) VALUES (2, '名古屋おでん', 'restaurant2.jpg', '気にいること間違いなし', 3000, 25000, '873-0145', '名古屋２', '012-345-678', '10:00', '21:00', 2);
INSERT IGNORE INTO restaurants (id, name, image_name, description, lowest_price, highest_price, postal_code, address, phone_number, opening_time, closing_time, category_id) VALUES (3, '名古屋カツ丼', 'restaurant3.jpg', '長時間の滞在可能です', 10000, 40000, '973-0145', '名古屋３', '012-345-678', '11:00', '0:00', 2);
INSERT IGNORE INTO restaurants (id, name, image_name, description, lowest_price, highest_price, postal_code, address, phone_number, opening_time, closing_time, category_id) VALUES (4, '名古屋ハヤシライス', 'restaurant4.jpg', 'ハヤシライス美味しいよ', 1000, 4000, '073-0145', '名古屋１', '012-345-678', '9:00', '21:00', 1); 
INSERT IGNORE INTO restaurants (id, name, image_name, description, lowest_price, highest_price, postal_code, address, phone_number, opening_time, closing_time, category_id) VALUES (5, '名古屋ひつまぶし', 'restaurant5.jpg', 'やっぱひつまぶしだよね', 3000, 25000, '873-0145', '名古屋２', '012-345-678', '10:00', '21:00', 2);
INSERT IGNORE INTO restaurants (id, name, image_name, description, lowest_price, highest_price, postal_code, address, phone_number, opening_time, closing_time, category_id) VALUES (6, '名古屋フレンチ', 'restaurant6.jpg', 'フレンチっていいよね', 10000, 40000, '973-0145', '名古屋３', '012-345-678', '11:00', '0:00', 4);
INSERT IGNORE INTO restaurants (id, name, image_name, description, lowest_price, highest_price, postal_code, address, phone_number, opening_time, closing_time, category_id) VALUES (7, '名古屋ミートスパゲッティ', 'restaurant7.jpg', 'スパゲッティといったらミートスパ', 1000, 4000, '073-0145', '名古屋１', '012-345-678', '9:00', '21:00', 3); 
INSERT IGNORE INTO restaurants (id, name, image_name, description, lowest_price, highest_price, postal_code, address, phone_number, opening_time, closing_time, category_id) VALUES (8, '名古屋ラーメン', 'restaurant8.jpg', 'とりあえずラーメン', 3000, 25000, '873-0145', '名古屋２', '012-345-678', '10:00', '21:00', 5);
INSERT IGNORE INTO restaurants (id, name, image_name, description, lowest_price, highest_price, postal_code, address, phone_number, opening_time, closing_time, category_id) VALUES (9, '名古屋寿司', 'restaurant9.jpg', '高級寿司', 10000, 40000, '973-0145', '名古屋３', '012-345-678', '11:00', '0:00', 6);
INSERT IGNORE INTO restaurants (id, name, image_name, description, lowest_price, highest_price, postal_code, address, phone_number, opening_time, closing_time, category_id) VALUES (10, '名古屋味噌カツ', 'restaurant10.jpg', '名古屋は味噌カツですよね', 10000, 40000, '973-0145', '名古屋３', '012-345-678', '11:00', '0:00', 2);

-- rolesテーブル
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_FREE');
INSERT IGNORE INTO roles (id, name) VALUES (2, 'ROLE_ADMIN');
INSERT IGNORE INTO roles (id, name) VALUES (3, 'ROLE_PREMIUM');

-- usersテーブル
INSERT IGNORE INTO users (id, name, email, password, postal_code, address, phone_number, role_id, enabled) VALUES (1, '侍 太郎', 'taro.samurai@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', '101-0022', '東京都千代田区神田練塀町300番地', '090-1234-5678',1, true);
INSERT IGNORE INTO users (id, name, email, password, postal_code, address, phone_number, role_id, enabled) VALUES (2, '侍 花子', 'hanako.samurai@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', '123-4567', '東京都千代田区神田練塀町300番地', '090-1234-5678', 2, true);
INSERT IGNORE INTO users (id, name, email, password, postal_code, address, phone_number, role_id, enabled) VALUES (3, '侍 義勝', 'yoshikatsu.samurai@example.com', 'password','638-0644','奈良県五條市西吉野町湯川X-XX-XX', '090-1234-5678', 1, false);
INSERT IGNORE INTO users (id, name, email, password, postal_code, address, phone_number, role_id, enabled) VALUES (4, '侍 幸美', 'sachimi.samurai@example.com', 'password', '342-0006','埼玉県吉川市南広島X-XX-XX', '090-1234-5678', 1, false);
INSERT IGNORE INTO users (id, name, email, password, postal_code, address, phone_number, role_id, enabled) VALUES (5, '侍 雅', 'miyabi.samurai@example.com', 'password','527-0209', '滋賀県東近江市佐目町X-XX-XX', '090-1234-5678',  3, false);



