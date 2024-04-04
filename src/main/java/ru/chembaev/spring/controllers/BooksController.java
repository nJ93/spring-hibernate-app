package ru.chembaev.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.chembaev.spring.models.Book;
import ru.chembaev.spring.models.Person;
import ru.chembaev.spring.services.BooksService;
import ru.chembaev.spring.util.PersonValidator;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BooksService booksService;
    private final PersonValidator personValidator;

    @Autowired
    public BooksController(BooksService booksService, PersonValidator booksValidator) {
        this.booksService = booksService;
        this.personValidator = booksValidator;
    }

    @GetMapping()
    public String index(@RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                        @RequestParam(value = "sort_by_year", required = false, defaultValue = "false") boolean isSortByYear,
                        Model model) {
        if (page != null && booksPerPage != null) {
            if (isSortByYear) {
                model.addAttribute("books", booksService.getSortedBooksWithPagination(page, booksPerPage));
            } else {
                model.addAttribute("books", booksService.getBooksWithPagination(page, booksPerPage));
            }
        } else if (isSortByYear) {
            model.addAttribute("books", booksService.findAllByOrderByPublishYear());
        } else {
            model.addAttribute("books", booksService.findAll());
        }
        return "books/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        model.addAttribute("book", booksService.findOne(id));

        Person owner = booksService.getOwnerInfo(id);

        if (owner != null) {
            model.addAttribute("ownerInfo", owner);
        } else {
            model.addAttribute("people", booksService.getAllPeoples());
        }
        return "books/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "books/new";

        booksService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", booksService.findOne(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "books/edit";

        booksService.update(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        booksService.delete(id);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/do")
    public String updateAffilation(@ModelAttribute("person") Person person,
                                   @PathVariable("id") int id) {
        booksService.updateAffilation(id, person);
        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/undo")
    public String deleteAffilation(@PathVariable("id") int id) {
        booksService.deleteAffilation(id);
        return "redirect:/books/" + id;
    }

    @GetMapping("/search")
    public String searchByName(@RequestParam(value = "bookName", required = false) String bookName, Model model) {
        if (bookName != null) {
            Optional<Book> foundBook = booksService.findByNameStartingWith(bookName);
            if (foundBook.isPresent()) {
                Book book = foundBook.get();
                Person owner = book.getOwner();
                if (owner != null) {
                    model.addAttribute("message", "Книга сейчас у:" + owner.getName());
                } else {
                    model.addAttribute("message", "Книга свободна");
                }
                model.addAttribute("book", book);
            } else {
                model.addAttribute("message", "Книг не найдено");
            }
        }
        return "books/search";
    }
}
