
insert into student (firstname, lastname, age)
values ('Ivan', 'Ivanov', 20),
       ('Fedor', 'Petrov', 19),
       ('Alexey', 'Popov', 25);

insert into teacher (firstname, lastname, faculty)
values ('Pavel', 'Pavlov', 'Technical'),
       ('Dmitriy', 'Sergeev', 'Business');

insert into course (title, teacher_id)
values ('Programming', 1),
       ('Mathmatics', 1),
       ('Marketing', 2);

insert into student_course (student_id, course_id)
values (1, 1),
       (1, 2),
       (2, 3),
       (3, 1),
       (3, 2),
       (3, 3);

