# mpt-auth-service
* Run Local
    ```console
    $ git clone https://github.com/LeeJusuk208/auth-server.git
    $ cd auth-server
    $ ./gradle_build.sh # Spring build with Gradle
    $ java -jar ./build/libs/auth-server-1.0.jar
    ```
* Before running the server, you need to create an application.yml file
    ```console
    $ vi ./auth-service/src/main/resources/application.yml
    ```
* application.yml
    ```yml
    OAuth2:
        google:
            url : https://accounts.google.com/o/oauth2/v2/auth
            client-id : "Your Google Web Application Client ID"
            client-secret : "Your Google Web Application Client Secret"
            callback-url : [URL Base]/api/oauth/login/google/redirection
            token-url : https://oauth2.googleapis.com/token
            userinfo-url : https://www.googleapis.com/oauth2/v1/userinfo
            scope : https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile
        kakao:
            url : https://kauth.kakao.com/oauth/authorize
            client-id : "Your Kakao Application Client ID"
            callback-url : [URL Base]/api/oauth/login/kakao/redirection
            token-url : https://kauth.kakao.com/oauth/token
            userinfo-url : https://kapi.kakao.com/v2/user/me
        naver:
            url : https://nid.naver.com/oauth2.0/authorize
            client-id : "Your Naver Application Client ID"
            client-secret : "Your Naver Application Client Secret"
            callback-url : [URL Base]/api/oauth/login/naver/redirection
            token-url : https://nid.naver.com/oauth2.0/token
            userinfo-url : https://openapi.naver.com/v1/nid/me
    Token:
        secret-key : "Your Secret Key"
    ```
