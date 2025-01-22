-- delete unnecessary colomn
ALTER TABLE locations
    DROP COLUMN type;
    
ALTER TABLE feedbacks
	DROP COLUMN visitor_comment;
    
    