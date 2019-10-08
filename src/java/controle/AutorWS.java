/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controle;

import dao.AutorDAO;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import modelo.Autor;
import util.FormataData;

/**
 *
 * @author dappo
 */
@WebServlet(name = "AutorWS", urlPatterns = {"/admin/autor/AutorWS"})
public class AutorWS extends HttpServlet {
       
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String acao = request.getParameter("txtAcao");
        RequestDispatcher destino;
        String pagina;
        AutorDAO dao = new AutorDAO();
        Autor obj;
        List<Autor> autores;
        Boolean deucerto;
        String msg;
        
        switch(String.valueOf(acao)){
            case "add":
                //Abrir tela e talvez buscar dados
                pagina = "add.jsp";
                break;
            case "edit":
                //Abrir tela e buscar dados
                obj = dao.buscarPorChavePrimaria(Long.parseLong(request.getParameter("txtId")));
                request.setAttribute("obj", obj);
                pagina = "edit.jsp";
                break;
            case "del":
                //excluir dados e buscar dados
                obj = dao.buscarPorChavePrimaria(Long.parseLong(request.getParameter("txtId")));
                deucerto = dao.excluir(obj);
                if(deucerto){
                    msg = obj.getNome() + " deletado com sucesso!";
                }else{
                    msg = "Problema ao excluir o genero " + obj.getNome();
                }
                autores = dao.listar();
                request.setAttribute("msg", msg);
                request.setAttribute("lista", autores);
                pagina = "list.jsp";
                break;
            default:
                //listar ou listar com filtro
                String filtro = request.getParameter("txtFiltro");
                if(filtro == null){
                    //lista todos
                    autores = dao.listar();
                }else{
                    //lista com filtro
                    try {                
                        autores = dao.listar(filtro);
                    } catch (Exception ex) {
                        autores = dao.listar();
                        msg = "Problema ao filtrar";
                        request.setAttribute("msg", msg);
                    }
                }
                request.setAttribute("lista", autores);
                pagina = "list.jsp";
                break;
        }
        
        destino = request.getRequestDispatcher(pagina);
        destino.forward(request, response);
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //criar variaveis
        Autor obj;
        AutorDAO dao = new AutorDAO();
        Boolean deucerto;
        String msg;
        String pagina;
        RequestDispatcher destino;
        List<Autor> autores;
        
        
        //Receber dados
        String id = request.getParameter("txtId");
        String nome = request.getParameter("txtNome");
        String nacionalidade = request.getParameter("txtNacionalidade");
        String data = request.getParameter("txtData");
        String foto = request.getParameter("txtFoto");
        
        
        //Tratar os dados (transformar os dados no formato solicitado)
        Date datanasc = FormataData.formata(data, "yyyy-MM-dd");
        
        if(id != null){
            //busca o que existe
           obj = dao.buscarPorChavePrimaria(Long.parseLong(id));
        }else{
            //cria um novo
           obj = new Autor(); 
        }
        
        //adicionar os dados recebidos
        obj.setNome(nome);
        obj.setDatanasc(datanasc);
        obj.setFoto(foto);
        obj.setNacionalidade(nacionalidade);
        
        if(id != null){
            deucerto = dao.alterar(obj);
            pagina = "list.jsp";
            autores = dao.listar();
            request.setAttribute("lista", autores);
            if(deucerto){
                msg = obj.getNome() + " alterado com sucesso!";
            }else{
                msg = "Problema ao editar o genero " + obj.getNome();
            }
        }else{
            deucerto = dao.incluir(obj);
            pagina = "add.jsp";
            if(deucerto){
                msg = obj.getNome() + " adicionado com sucesso!";
            }else{
                msg = "Problema ao adicionar o genero " + obj.getNome();
            }
        }
        request.setAttribute("msg", msg);
        destino = request.getRequestDispatcher(pagina);
        destino.forward(request, response);
        
        
    }

}
