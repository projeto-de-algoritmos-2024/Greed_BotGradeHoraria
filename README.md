[//]: # (**!! Atenção: Renomeie o seu repositório para &#40;Tema&#41;_&#40;NomeDoProjeto&#41;. !!** )

[//]: # ()
[//]: # (Temas:)

[//]: # ( - Grafos1)

[//]: # ( - Grafos2)

[//]: # ( - PD)

[//]: # ( - D&C)

[//]: # ( - Greed)

[//]: # ( - Final )

[//]: # ( )
[//]: # ( **!! *Não coloque os nomes dos alunos no título do repositório*. Exemplo de título correto: Grafos2_Labirinto-do-Minotauro !!**)

[//]: # ( )
[//]: # ( &#40;Apague essa seção&#41;)

# Bot Grade Horária

**Número da Lista**: 13<br>
**Conteúdo da Disciplina**: Greed<br>

## Alunos
| Matrícula  | Aluno                         |
|------------|-------------------------------|
| 22/1007814 | André Emanuel Bispo da Silva  |
| 22/1008150 | João Antonio Ginuino Carvalho |

## Sobre 

Este projeto consiste em um bot para o Telegram que gera todas as possíveis grades horárias, verificando e resolvendo conflitos de agendamento de intervalos (interval scheduling).

> Para ver o vídeo da apresentação clique [aqui](https://youtu.be/dBowU2K6534).

## Screenshots
<div align="center"><img src= "https://raw.githubusercontent.com/projeto-de-algoritmos-2024/Greed_BotGradeHoraria/refs/heads/main/images/inicia.png?raw=true"/></div>

<center>
Figura 1 - Iniciando bot
</center>

<div align="center"><img src= "https://raw.githubusercontent.com/projeto-de-algoritmos-2024/Greed_BotGradeHoraria/refs/heads/main/images/escolhe.png?raw=true"/></div>

<center>
Figura 2 - Escolhendo Matérias
</center>

<div align="center"><img src= "https://raw.githubusercontent.com/projeto-de-algoritmos-2024/Greed_BotGradeHoraria/refs/heads/main/images/grade.png?raw=true"/></div>

<center>
Figura 3 - Geação da grade
</center>

## Instalação 
**Linguagem**: Kotlin<br>

Para executar o projeto, siga os passos abaixo:

1.  **Restaurar o banco de dados:** Faça o download do dump do banco de dados disponível [aqui](./postgres_localhost-2025_01_18_12_54_32-dump.sql) e restaure-o em seu servidor PostgreSQL utilizando o nome "greed".

2.  **Criar o usuário do banco de dados:** Execute o seguinte comando SQL para criar o usuário com as permissões necessárias:

    ```sql
    CREATE ROLE kd_user LOGIN PASSWORD 'pa';
    GRANT ALL PRIVILEGES ON DATABASE greed TO kd_user; 
    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO kd_user;
    ```

3.  **Compilar o projeto:** Execute o comando `gradle build` no diretório raiz do projeto para compilar o código.

4.  **Configurar as variáveis de ambiente:** Defina as variáveis de ambiente necessárias para o projeto. Consulte a documentação do projeto (se houver) para obter a lista completa de variáveis. No mínimo, você precisará configurar o token do bot do Telegram:

    ```bash
    export BOT_TOKEN=7713139715:AAF-EoXnYyogQDKwD3UcId2f3uDiprg7t7M
    ```
    Ou, se estiver usando Windows (PowerShell):
    ```powershell
    $env:BOT_TOKEN = "7713139715:AAF-EoXnYyogQDKwD3UcId2f3uDiprg7t7M"
    ```

## Uso 

Após rodar a main basta acessar o link do bot disponível em https://web.telegram.org/k/#@UnBHorarios_bot.

### Apresentação

> Para ver o video de apresentação clique [aqui](https://youtu.be/dBowU2K6534).




