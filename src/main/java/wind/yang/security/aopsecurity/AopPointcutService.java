package wind.yang.security.aopsecurity;

import org.springframework.stereotype.Service;

@Service
public class AopPointcutService {
    // pointcut method보안 적용
    public void pointcutSecure(){
        System.out.println("pointcutSecured");
    }

    public void notSecured(){
        System.out.println("i'm unsecured method");
    }
}
