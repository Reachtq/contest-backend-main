# contest-backend


Программа хранит данные в БД, предоставляет API для клиентской части приложения. <br />
Основные функции программы: <br />
Аутентификация на основе логина и пароля, в ответ система возвращает JWT токен(действует 12 часов), который служит для авторизации пользователя. <br />
В программе представлено две роли, студент и преподаватель. <br />
Студент имеет возможность просматривать свои курсы, задания, отправлять на них решения, смотреть предыдущие попытки о оценки по заданиям. <br />
Преподаватель может: <br />
•	Создавать курсы и задания, <br />
•	Создавать группы и добавлять в них студентов, <br />
•	Добавлять группы на курс,  <br /> 
•	Редактировать задания,  <br />
•	Оставлять комментарии к заданиям, <br />
•	Оценивать задания, <br />
•	Удалять комментарии. <br />
Существует 3 типа заданий: <br />
•	Тестовые задания с 1 или множественным ответом, <br />
•	Задания на написание SQL кода, проверяемые автоматически, <br />
•	Задания на написание SQL кода, проверяемые вручную <br />
Для каждого типа задания существует отдельный запрос на добавление решения, причем у задания проверяемых автоматически есть возможность отправить решение не на оценку, а как попытку, тогда сервер вернет двумерный массив. 
<br />

