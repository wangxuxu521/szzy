-- Add rating_count column to resource table
ALTER TABLE `resource` 
ADD COLUMN `rating_count` int(11) NOT NULL DEFAULT '0' COMMENT '评分人数' AFTER `rating`;

-- Update existing resources with rating count from resource_rating table
UPDATE `resource` r 
SET r.rating_count = (
    SELECT COUNT(*) 
    FROM `resource_rating` rr 
    WHERE rr.resource_id = r.resource_id
); 