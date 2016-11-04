# vkApiTask
#


Для работы приложения скопируйте `access_token` полученный после авторизации и вставьте его в переменную `token`
##### Activity/StartActivity.java
###
```
16    String token = "[your_token]" // Ваш токен - [your_token]  
```

##### Activity/Chat.java
###
```
463   String attach = "photo[owner_id]_"+ id; // Идентификатор вашей страницы vk  - [owner_id]
```

