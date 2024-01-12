package com.chahoo.datauploader.util;

import java.util.Scanner;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActionResolver {
    private final DataManager dataManager;

    public void selectAction(){

        Scanner scanner = new Scanner(System.in);
        boolean roop = true;
        while(roop) {

            System.out.print(
            "\n\n"
            +"0 . 스키마생성\n"
            +"1 . 층 업로드\n"
            +"2 . 부재 업로드 (벽체 제외)\n"
            +"3 . \n"
            +"4 . 태그 연결\n"
            +"5 . 프로그램 종료\n"
            +"\n"
            +":: "
            );
    
            int select = scanner.nextInt();
    
            dataManager.convertProperties();

            switch(select){
                case 0:
                    dataManager.createSchema();
                    break;
                case 1:
                    dataManager.uploadFloors();
                    break;
                case 2:
                    dataManager.uploadMaterials();
                    break;
                case 4 :
                    dataManager.matchTags();
                    break;
                case 5 :roop = false;
                default:
                System.out.println("\n\n알맞은 값을 입력해주세요.\n\n");
            }
        };
        
        scanner.close();

        System.out.println("종료종료 왕종료");
        System.exit(0);


    }
    
}
